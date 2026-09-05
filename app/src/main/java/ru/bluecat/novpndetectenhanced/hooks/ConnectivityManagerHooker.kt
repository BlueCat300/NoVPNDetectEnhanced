package ru.bluecat.novpndetectenhanced.hooks

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.classOf
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import ru.bluecat.novpndetectenhanced.printLogs

/**
 * The reactive way to detect a VPN. Apps subscribe to network events through a NetworkCallback
 * registered with a VPN-targeting NetworkRequest, and treat onAvailable of such a network as a
 * VPN presence flag.
 * https://developer.android.com/reference/android/net/ConnectivityManager
 */
object ConnectivityManagerHooker : YukiBaseHooker() {

    private val connManager = classOf<ConnectivityManager>()
    private val netCallback = classOf<ConnectivityManager.NetworkCallback>()

    private val registerMethods = setOf(
        "registerNetworkCallback",
        "requestNetwork",
        "registerDefaultNetworkCallback",
        "registerBestMatchingNetworkCallback"
    )

    override fun onHook() {
        hookGetNetworkCapabilities()
        hookRegisterCallbacks()
    }

    private fun hookGetNetworkCapabilities() {
        val method = connManager.resolve()
            .optional(true)
            .firstMethodOrNull { name = "getNetworkCapabilities" } ?: return

        method.hook().after {
            result<NetworkCapabilities>()?.let { capabilities ->
                if (isVpn(capabilities)) {
                    printLogs("Hooked: ConnectivityManager.getNetworkCapabilities() -> null")
                    resultNull()
                }
            }
        }
    }

    private fun hookRegisterCallbacks() {
        val methods = connManager.resolve()
            .optional(true)
            .method {
                name { it in registerMethods }
                parameters { params -> params.any { netCallback.isAssignableFrom(it) } }
            }

        methods.forEach { method ->
            method.hook().before {
                val request = args.firstOrNull { it is NetworkRequest } as? NetworkRequest
                // Only intercept callbacks whose request explicitly targets VPN, so ordinary
                // connectivity handling of other callbacks is left untouched.
                if (!isVpnRequest(request)) return@before

                args.forEachIndexed { index, arg ->
                    if (arg is ConnectivityManager.NetworkCallback) {
                        printLogs("Hooked: ConnectivityManager.register(VPN NetworkCallback) -> Muted")
                        args(index).set(muteCallback())
                    }
                }
            }
        }
    }

    /**
     * Reads the raw transports of the request through NetworkRequest.networkCapabilities and
     * NetworkCapabilities.getTransportTypes(), bypassing the already-hooked hasTransport().
     */
    private fun isVpnRequest(request: NetworkRequest?): Boolean {
        val capabilities = request?.asResolver()
            ?.optional(true)
            ?.firstFieldOrNull { name = "networkCapabilities" }
            ?.get<NetworkCapabilities>() ?: return false
        return isVpn(capabilities)
    }

    private fun isVpn(capabilities: NetworkCapabilities): Boolean {
        // capabilities.asResolver() already binds the instance, so invoke() is called directly
        // without of() to avoid "Instance already set" on the reused resolver.
        val transports = capabilities.asResolver()
            .optional(true)
            .firstMethodOrNull { name = "getTransportTypes" }
            ?.invoke<IntArray>() ?: return false
        return transports.contains(NetworkCapabilities.TRANSPORT_VPN)
    }

    /**
     * A NetworkCallback proxy for a VPN-targeting request. Every event is dropped so the VPN network
     * never "appears" to the app, keeping its VPN state flag false.
     */
    private fun muteCallback() = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {}
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {}
        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {}
        override fun onLosing(network: Network, maxMsToLive: Int) {}
        override fun onLost(network: Network) {}
        override fun onUnavailable() {}
    }
}
