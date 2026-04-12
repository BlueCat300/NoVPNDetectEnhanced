#include "jni.h"
#include "hooker.hpp"
#include "log.hpp"
#include <ifaddrs.h>
#include <dlfcn.h>
#include <random>

static int (*orig_getifaddrs)(struct ifaddrs **ifap);

bool IsVpnInterface(const char* name) {
    if (!name) return false;

    std::string_view sv(name);
    return sv.starts_with("tun") ||
           sv.starts_with("ppp") ||
           sv.starts_with("pptp")  ||
           sv.starts_with("wg") ||
           sv.starts_with("ipsec");
}

const char* GetRandomName() {
    using namespace std::literals;

    static constexpr std::array names {
        "dummy0"sv,
        "rmnet0"sv,
        "rmnet_data0"sv,
        "eth0"sv,
        "wlan0"sv
    };
    static std::mt19937 gen {std::random_device{}()};

    auto index = std::uniform_int_distribution<size_t>{0, names.size() - 1}(gen);
    return names[index].data();
}

/**
 * This option can be used mostly by applications on cross-platform frameworks.
 * Checking interface names via getifaddrs in libc.so
 */
int FilterInterfaces(struct ifaddrs **ifap) {
    int result = orig_getifaddrs(ifap);

    if (result == 0 && ifap != nullptr && *ifap != nullptr) {
        struct ifaddrs *current = *ifap;

        while (current != nullptr) {
            if (IsVpnInterface(current->ifa_name)) {
                auto spoofed_name = GetRandomName();
                PrintLogs("Hooked: Native | getifaddrs() {} -> {}", current->ifa_name, spoofed_name);
                current->ifa_name = (char*) spoofed_name;
            }
            current = current->ifa_next;
        }
    }
    return result;
}

extern "C" [[gnu::visibility("default")]] [[gnu::used]]
NativeOnModuleLoaded native_init(const NativeAPIEntries *entries) {
    void* pointer = dlsym(RTLD_DEFAULT, "getifaddrs");

    if (pointer != nullptr) {
        entries->hook_func(pointer, (void *) FilterInterfaces, (void **)&orig_getifaddrs);
    }
    return nullptr;
}