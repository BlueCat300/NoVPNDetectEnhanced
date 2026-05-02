package ru.bluecat.novpndetectenhanced

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import ru.bluecat.novpndetectenhanced.hooks.LinkPropertiesHooker
import ru.bluecat.novpndetectenhanced.hooks.NetworkCapabilitiesHooker
import ru.bluecat.novpndetectenhanced.hooks.NetworkInfoHooker
import ru.bluecat.novpndetectenhanced.hooks.NetworkInterfaceHooker

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {

    override fun onInit() = YukiHookAPI.configs {
        debugLog {
            tag = "NoVPNDetect Enhanced"
        }
        isEnableDataChannel = false
    }

    override fun onHook() = YukiHookAPI.encase {
        loadApp {
            if (mainProcessName == "android") return@encase

            withProcess(mainProcessName) {
                System.loadLibrary("lspd-native")

                val hooks = setOf(
                    NetworkInfoHooker,
                    NetworkCapabilitiesHooker,
                    NetworkInterfaceHooker,
                    LinkPropertiesHooker
                )
                hooks.forEach { loadHooker(it) }
            }
        }
    }
}