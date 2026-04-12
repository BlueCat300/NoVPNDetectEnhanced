@file:Suppress("DEPRECATION")

package ru.bluecat.novpndetectenhanced.hooks

import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.classOf
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import ru.bluecat.novpndetectenhanced.printLogs

/**
 * Deprecated.
 * https://developer.android.com/reference/android/net/NetworkInfo
 */
object NetworkInfoHooker : YukiBaseHooker() {

    private val netInfo = classOf<NetworkInfo>()

    override fun onHook() {
        hookGetType()
        hookGetTypeName()
    }

    private fun hookGetType() {
        for (methodName in listOf("getType", "getSubtype")) {
            val method = netInfo.resolve()
                .optional(true)
                .firstMethodOrNull { name = methodName } ?: continue

            method.hook().after {
                result<Int>()?.let { networkType ->
                    if (networkType == ConnectivityManager.TYPE_VPN) {
                        printLogs("Hooked: NetworkInfo.$methodName() -> TYPE_WIFI")
                        result = ConnectivityManager.TYPE_WIFI
                    }
                }
            }
        }
    }

    private fun hookGetTypeName() {
        for (methodName in listOf("getTypeName", "getSubtypeName")) {
            val method = netInfo.resolve()
                .optional(true)
                .firstMethodOrNull { name = methodName } ?: continue

            method.hook().after {
                result<String>()?.let { typeName ->
                    if (typeName.contains("VPN", true)) {
                        printLogs("Hooked: NetworkInfo.$methodName() -> TYPE_WIFI")
                        result = "WIFI"
                    }
                }
            }
        }
    }
}