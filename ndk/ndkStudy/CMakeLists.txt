cmake_minimum_required(VERSION 3.4.1)

#热修复配置

add_library( android-fix-lib
             SHARED
        src/main/cpp/fix/android_fix.cc)

find_library( log-lib
              log )

target_link_libraries( android-fix-lib
                       ${log-lib})