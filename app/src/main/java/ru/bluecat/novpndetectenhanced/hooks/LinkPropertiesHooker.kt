package ru.bluecat.novpndetectenhanced.hooks

import android.net.LinkProperties
import android.net.RouteInfo
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.classOf
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import ru.bluecat.novpndetectenhanced.printLogs
import ru.bluecat.novpndetectenhanced.vpnInterfaces

/**
 * Rarely used ways to determine VPN through routes and DNS.
 * https://developer.android.com/reference/android/net/LinkProperties
 */
object LinkPropertiesHooker : YukiBaseHooker() {

    private val properties = classOf<LinkProperties>()

    override fun onHook() {
        hookGetInterfaceName()
        hookRoutes()
    }

    private fun hookGetInterfaceName() {
        val method = properties.resolve()
            .optional(true)
            .firstMethodOrNull { name = "getInterfaceName" } ?: return

        method.hook().after {
            result<String>()?.let { name ->
                if (vpnInterfaces.any { name.startsWith(it) }) {
                    printLogs("Hooked: LinkProperties.getInterfaceName($name) -> null")
                    resultNull()
                }
            }
        }
    }

    private fun hookRoutes() {
        val method = properties.resolve()
            .optional(true)
            .firstMethodOrNull { name = "getRoutes" } ?: return

        method.hook().after {
            result<List<RouteInfo>>()?.let { routes ->
                val newRoutes = routes.filterNot { info ->
                    vpnInterfaces.any { (info.`interface` ?: "").startsWith(it) }
                }
                printLogs("Hooked: LinkProperties.getRoutes() -> Filtered")
                result = newRoutes
            }
        }
    }
}