#include <android/log.h>
#include <format>
#include <unordered_set>

static std::unordered_set<std::string> log_collector;
template<typename... Args>

void PrintLogs(std::string_view msg, Args&&... args) {
    auto proc_name = getprogname();
    if (log_collector.find(proc_name) == log_collector.end()) {
        auto full_msg = std::vformat(msg, std::make_format_args(args...));
        auto log = std::format("[NoVpnDetect Enhanced][I][{}] {}", proc_name, full_msg);
        log_collector.insert(proc_name);

        __android_log_print(ANDROID_LOG_INFO, "LSPosed-Bridge", "%s", log.c_str());
    }
}
