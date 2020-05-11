cmake_minimum_required(VERSION 3.4.1)
file(GLOB my_source src/main/cpp/*.cpp src/main/cpp/*.c)
set(distribution_DIR ../../../../libs)

add_subdirectory(src/main/cpp/librtmp)

include_directories(libs/include)
add_library( x264
             STATIC
             IMPORTED)

set_target_properties(x264
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/armeabi/libx264.a)
add_library(faac
             STATIC
             IMPORTED)

set_target_properties(faac
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/armeabi/libfaac.a)
add_library(
             native-lib
             SHARED
             src/main/cpp/native-lib.cpp )
find_library(
              log-lib
              log )

target_link_libraries(
                       native-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib}
                        faac
                        x264
                        rtmp
                        )