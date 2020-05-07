# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html
cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             nativeDump

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native.cpp
             src/main/cpp/dlopen.c
             src/main/cpp/inlineHook.c
             src/main/cpp/relocate.c
             src/main/cpp/And64InlineHook.cpp
             src/main/cpp/util/deviceutils.cpp
              src/main/cpp/util/fileutils.cpp)

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

target_link_libraries( # Specifies the target library.
                       nativeDump

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )


if(${ANDROID_ABI} STREQUAL "armeabi-v7a")
    include_directories(${ANDROID_SYSROOT}/usr/include/arm-linux-androidabi)
elseif(${ANDROID_ABI} STREQUAL "arm64-v8a")
    include_directories(${ANDROID_SYSROOT}/usr/include/aarch64-linux-android)
else()
    include_directories(${ANDROID_SYSROOT}/usr/include/arm-linux-androidabi)
endif()