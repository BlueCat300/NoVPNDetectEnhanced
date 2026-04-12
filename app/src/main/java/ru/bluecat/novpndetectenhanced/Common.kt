package ru.bluecat.novpndetectenhanced

import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.log.YLog.EnvType

val vpnInterfaces = setOf("tun", "ppp", "pptp", "wg", "ipsec")
private val logCollector = mutableSetOf<String>()

fun printLogs(msg: String) {
    if (!logCollector.contains(msg)) {
        YLog.info(msg, env = EnvType.XPOSED_ENVIRONMENT)
        logCollector.add(msg)
    }
}