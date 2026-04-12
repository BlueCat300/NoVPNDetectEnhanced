@file:Suppress("DEPRECATION")

package ru.bluecat.novpndetectenhanced.hooks

import android.net.ConnectivityManager
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.classOf
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import ru.bluecat.novpndetectenhanced.printLogs

/**
 * Deprecated.
 * https://developer.android.com/reference/android/net/ConnectivityManager
 */
object ConnectivityManagerHooker : YukiBaseHooker() {

    override fun onHook() {
        val method = classOf<ConnectivityManager>().resolve()
            .optional(true)
            .firstMethodOrNull { name = "getNetworkInfo" } ?: return

        method.hook().after {
            args().first().int().let { networkType ->
                if (networkType == ConnectivityManager.TYPE_VPN) {
                    printLogs("Hooked: ConnectivityManager.getNetworkInfo($networkType) -> null)")
                    resultNull()
                }
            }
        }
    }
}