cmake_minimum_required(VERSION 3.4.1)

#热更新配置
file(GLOB c_path src/main/cpp/bzip2/*.c)

add_library( native-lib
             SHARED
             ${c_path}
             src/main/cpp/bspatch.c)

find_library( log-lib
              log )

target_link_libraries( native-lib
                       ${log-lib})
