<div align="center">
<h3><a href="https://github.com/BlueCat300/NoVPNDetectEnhanced/blob/master/README_en.md">English</a></h3>
<h1>NoVPNDetect Enhanced</h1>

![downloads](https://img.shields.io/github/downloads/Xposed-Modules-Repo/ru.bluecat.novpndetectenhanced/total)
[![GitHub release](https://img.shields.io/github/v/release/Xposed-Modules-Repo/ru.bluecat.novpndetectenhanced)](https://github.com/Xposed-Modules-Repo/ru.bluecat.novpndetectenhanced/releases)
[![GitHub Release Date](https://img.shields.io/github/release-date/Xposed-Modules-Repo/ru.bluecat.novpndetectenhanced)](https://github.com/Xposed-Modules-Repo/ru.bluecat.novpndetectenhanced/releases)
[![Telegram](https://img.shields.io/badge/Telegram-Channel-blue.svg?logo=telegram)](https://t.me/lsposed_workshop)
[![Telegram Group](https://img.shields.io/badge/Telegram-Group-blue.svg?logo=telegram)](https://t.me/lsposed_workshop_forum)
[![4PDA](https://img.shields.io/badge/4PDA-Topic-blue)](https://4pda.to/forum/index.php?showtopic=603033)
[![Donate](https://img.shields.io/badge/Donate_Form-blue)](https://pay.cloudtips.ru/p/85f8cf00)

<p>Скрытие различных локальных обнаружений VPN через публичный API от приложений на android устройствах.</p>
</div>

### Описание:
Модуль использует методики [данного модуля](https://bitbucket.org/yuri-project/novpndetect/src/main/) со значительным расширением возможностей. Перехватывается значительно большее количество публичных API, а так же C++ подход.
Модуль выводит в LSPosed лог все виды API к которому обращается приложение.

Благодарности разработчикам ниже за нахождение различных путей обнаружений:
- [VPN Detector](https://github.com/cherepavel/VPN-Detector)
- [YourVPNDead](https://github.com/loop-uh/yourvpndead)
- [RKNHardering](https://github.com/xtclovver/RKNHardering)

Вы так же можете проверять эффективность модуля на этих приложениях, однако помните что практика(реальные приложения) это лучший путь тестирования. В дополнение к этому модулю рекомендуется использовать [PortGuard](https://4pda.to/forum/index.php?showtopic=915158&view=findpost&p=142838199) c версии 1.0.1, для защиты локальных портов.

### Обратите внимание:
- Модуль не будет работать если у вас на устройстве установлен zygisk модуль PMPatch из Lucky Patcher для патчинга PackageManager. Приложения будут вылетать. Убедитесь что он не установлен на устройстве, можно заменить на lsposed вариант патчинга.
- Модуль не будет работать если у подключаемого приложения есть защита от LSPosed, проверка на инъекции в память. Например MirPay, Т-Банк.
- Модуль имеет возможность работать в режиме [LSPatch](https://github.com/JingMatrix/LSPatch). Не имеет настроек, достаточно пересобрать приложение. Приложение не должно иметь защиту по подписи или пересборки.


### Скрываемые публичные API:
- NetworkCapabilities: hasTransport(), getCapabilities(), hasCapability(), toString()
- NetworkInterface: isVirtual(), getName(), getByName(), isUp(), getNetworkInterfaces(), getMTU()
- LinkProperties: getInterfaceName(), getRoutes()
- ConnectivityManager: getNetworkInfo()
- NetworkInfo: getType(), getSubtype(), getTypeName(), getSubtypeName()

### Скрываемые Native(C++):
- getifaddrs() > libc.so

### Скачать:
- [LSPosed репозиторий](https://github.com/Xposed-Modules-Repo/ru.bluecat.novpndetectenhanced/releases)
- [4PDA](https://4pda.to/forum/index.php?showtopic=603033)

