cmake_minimum_required(VERSION 3.4.1)

#热修复配置

add_library( android-fix-lib
             SHARED
        src/main/cpp/fix/android_fix.cc)

find_library( log-lib
              log )

target_link_libraries( android-fix-lib
                       ${log-lib})

#多进程守护
add_library( process-Daemon-lib
        SHARED
        src/main/cpp/other/signal_process.cc)

find_library( log-lib
        log )

target_link_libraries( process-Daemon-lib
        ${log-lib})