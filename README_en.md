<div align="center">
<h1>NoVPNDetect Enhanced</h1>

![downloads](https://img.shields.io/github/downloads/Xposed-Modules-Repo/ru.bluecat.novpndetectenhanced/total)
[![GitHub release](https://img.shields.io/github/v/release/Xposed-Modules-Repo/ru.bluecat.novpndetectenhanced)](https://github.com/Xposed-Modules-Repo/ru.bluecat.novpndetectenhanced/releases)
[![GitHub Release Date](https://img.shields.io/github/release-date/Xposed-Modules-Repo/ru.bluecat.novpndetectenhanced)](https://github.com/Xposed-Modules-Repo/ru.bluecat.novpndetectenhanced/releases)
[![Telegram](https://img.shields.io/badge/Telegram-Channel-blue.svg?logo=telegram)](https://t.me/lsposed_workshop)

<p>Prevent some apps detect your phone connected to VPN.</p>
</div>

### Description:
The module uses the techniques of [this module](https://bitbucket.org/yuri-project/novpndetect/src/main/) with significantly expanded capabilities. It intercepts a significantly larger number of public APIs, as well as a C++ approach.
The module outputs all APIs accessed by the application to the LSPosed log.

Thanks to the developers below for finding various detection ways:
- [VPN Detector](https://github.com/cherepavel/VPN-Detector)
- [YourVPNDead](https://github.com/loop-uh/yourvpndead)
- [RKNHardering](https://github.com/xtclovver/RKNHardering)

You can also test the module's effectiveness on these applications, but remember that practice (real applications) is the best way to test.

### Attention:
- The module won't work if you have the PMPatch zygisk module from Lucky Patcher installed on your device for patching PackageManager. Apps will crash. Make sure it's not installed on your device, you can replace it with the lsposed patching option.
- The module will not work if application has LSPosed protection, checking for memory injection.
- The module may not intercept some low-level techniques used by applications built on cross-platform frameworks, including webview (pure web interface).
- The module can operate in [LSPatch](https://github.com/JingMatrix/LSPatch) mode. No settings are required, simply rebuilding the application is sufficient. The application must not be protected by signature or rebuilding checking.


### Hidden public API:
- NetworkCapabilities: hasTransport(), getCapabilities(), hasCapability(), toString()
- NetworkInterface: isVirtual(), getName(), getByName(), isUp(), getNetworkInterfaces(), getMTU()
- LinkProperties: getInterfaceName(), getRoutes()
- ConnectivityManager: getNetworkInfo()
- NetworkInfo: getType(), getSubtype(), getTypeName(), getSubtypeName()

### Hidden Native(C++):
- getifaddrs() > libc.so

### Download:
- [LSPosed repository](https://github.com/Xposed-Modules-Repo/ru.bluecat.novpndetectenhanced/releases)

