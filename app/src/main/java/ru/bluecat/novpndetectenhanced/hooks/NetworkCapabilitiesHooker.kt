package ru.bluecat.novpndetectenhanced.hooks

import android.annotation.SuppressLint
import android.net.NetworkCapabilities
import android.net.TransportInfo
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.classOf
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import ru.bluecat.novpndetectenhanced.printLogs

/**
 * The primary public API for detecting the presence of a VPN. Modern OS versions primarily use this class.
 * https://developer.android.com/reference/android/net/NetworkCapabilities
 */
object NetworkCapabilitiesHooker : YukiBaseHooker() {

    private val NetCapabilities = classOf<NetworkCapabilities>()

    override fun onHook() {
        hookHasTransport()
        hookGetCapabilities()
        hookHasCapability()
        hookTransportInfo()
        hookDebugInfo()
    }

    private fun hookHasTransport() {
        val method = NetCapabilities.resolve()
            .optional(true)
            .firstMethodOrNull { name = "hasTransport" } ?: return

        method.hook().after {
            args().first().int().let { transportType ->
                if (transportType == NetworkCapabilities.TRANSPORT_VPN) {
                    printLogs("Hooked: NetworkCapabilities.hasTransport($transportType) -> false")
                    resultFalse()
                }
            }
        }
    }

    private fun hookGetCapabilities() {
        val method = NetCapabilities.resolve()
            .optional(true)
            .firstMethodOrNull { name = "getCapabilities" } ?: return

        method.hook().after {
            result<IntArray>()?.let { networks ->
                if (!networks.contains(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)) {
                    printLogs("Hooked: NetworkCapabilities.getCapabilities() -> NET_CAPABILITY_NOT_VPN")
                    result = IntArray(networks.size + 1).also { newResult ->
                        networks.forEachIndexed { index, i ->
                            newResult[index] = i
                        }
                        newResult[newResult.size - 1] = NetworkCapabilities.NET_CAPABILITY_NOT_VPN
                    }
                }
            }
        }
    }

    private fun hookHasCapability() {
        val method = NetCapabilities.resolve()
            .optional(true)
            .firstMethodOrNull { name = "hasCapability" } ?: return

        method.hook().after {
            args().first().int().let { capability ->
                if (capability == NetworkCapabilities.NET_CAPABILITY_NOT_VPN) {
                    printLogs("Hooked: NetworkCapabilities.hasCapability($capability) -> true")
                    resultTrue()
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private fun hookTransportInfo() {
        val method = NetCapabilities.resolve()
            .optional(true)
            .firstMethodOrNull { name = "getTransportInfo" } ?: return

        method.hook().after {
            result<TransportInfo>()?.let { info ->
                if (info::class.java.name.endsWith("VpnTransportInfo")) {
                    printLogs("Hooked: NetworkCapabilities.getTransportInfo() -> null")
                    resultNull()
                }
            }
        }
    }

    private fun hookDebugInfo() {
        val method = NetCapabilities.resolve()
            .optional(true)
            .firstMethodOrNull { name = "toString" } ?: return

        method.hook().after {
            result<String>()?.let { debugString ->
                var debugInfo = debugString

                if (debugString.contains("VpnTransportInfo")) {
                    debugInfo = replaceBetween(debugInfo, "TransportInfo: ", "}>", "null")
                }
                if (!debugString.contains("NOT_VPN")) {
                    debugInfo = replaceBetween(debugInfo, "Capabilities", "NOT_METERED&", ": NOT_VPN&")
                }
                printLogs("Hooked: NetworkCapabilities.toString() -> Filtered")
                result = debugInfo
            }
        }
    }

    private fun replaceBetween(target: String, start: String, end: String, newValue: String): String {
        val prefix = target.substringBefore(start) + start
        val suffix = target.substringAfter(end)
        return prefix + newValue + suffix
    }
}