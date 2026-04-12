package ru.bluecat.novpndetectenhanced.hooks

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.classOf
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import ru.bluecat.novpndetectenhanced.printLogs
import ru.bluecat.novpndetectenhanced.vpnInterfaces
import java.net.NetworkInterface
import java.util.Collections
import java.util.Enumeration

/**
 * Here's everything about network interface names. A less popular way than NetworkCapabilities.
 * https://developer.android.com/reference/java/net/NetworkInterface
 */
object NetworkInterfaceHooker : YukiBaseHooker() {

    private val netInterface = classOf<NetworkInterface>()
    private val renamedInterfaces = HashMap<String, String>()

    override fun onHook() {
        hookIsVirtual()
        hookGetName()
        hookGetByName()
        hookIsUp()
        hookNetworkInterfaces()
        hookGetMTU()
    }

    private fun hookIsVirtual() {
        val method = netInterface.resolve()
            .optional(true)
            .firstMethodOrNull { name = "isVirtual" } ?: return

        method.hook().after {
            printLogs("Hooked: NetworkInterface.isVirtual() -> false)")
            resultFalse()
        }
    }

    private fun hookGetName() {
        val method = netInterface.resolve()
            .optional(true)
            .firstMethodOrNull { name = "getName" } ?: return

        method.hook().after {
            result<String>()?.let { name ->
                if (vpnInterfaces.any { name.startsWith(it) }) {
                    createInterfaceName(name)
                    printLogs("Hooked: NetworkInterface.getName() -> ${renamedInterfaces[name]}")
                    result = renamedInterfaces[name]
                }
            }
        }
    }

    private fun hookGetByName() {
        val method = netInterface.resolve()
            .optional(true)
            .firstMethodOrNull { name = "getByName" } ?: return

        method.hook().before {
            args().first().string().let { name ->
                if (vpnInterfaces.any { name.startsWith(it) }) {
                    createInterfaceName(name)
                    printLogs("Hooked: NetworkInterface.getByName($name) -> ${renamedInterfaces[name]}")
                    args().first().set(renamedInterfaces[name])
                }
            }
        }
    }

    private fun createInterfaceName(name: String) {
        if (!renamedInterfaces.keys.contains(name)) {
            val allowedChars = ('a'..'z') + ('0'..'9')
            renamedInterfaces[name] = (1 .. name.length)
                .map { allowedChars.random() }
                .joinToString("")
        }
    }

    private fun hookIsUp() {
        val method = netInterface.resolve()
            .optional(true)
            .firstMethodOrNull { name = "isUp" } ?: return

        method.hook().after {
            instance<NetworkInterface>().name.let { name ->
                if (vpnInterfaces.any { name.startsWith(it) }) {
                    printLogs("Hooked: NetworkInterface.isUp() -> false")
                    resultFalse()
                }
            }
        }
    }

    private fun hookNetworkInterfaces() {
        val method = netInterface.resolve()
            .optional(true)
            .firstMethodOrNull { name = "getNetworkInterfaces" } ?: return

        method.hook().after {
            result<Enumeration<NetworkInterface>>()?.let { interfaces ->
                val list = interfaces.toList().filterNot { iface ->
                    vpnInterfaces.any { iface.name.startsWith(it) }
                }
                printLogs("Hooked: NetworkInterface.getNetworkInterfaces() -> Filtered")
                result = Collections.enumeration(list)
            }
        }
    }

    private fun hookGetMTU() {
        val method = netInterface.resolve()
            .optional(true)
            .firstMethodOrNull { name = "getMTU" } ?: return

        method.hook().after {
            result<Int>()?.let { mtu ->
                if (mtu in 1200..1430) {
                    printLogs("Hooked: NetworkInterface.getMTU() $mtu -> 1500")
                    result = 1500
                }
            }
        }
    }
}