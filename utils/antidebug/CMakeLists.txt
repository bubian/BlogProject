cmake_minimum_required(VERSION 3.4.1)

add_library(
        AntiSafe
        SHARED
        src/main/cpp/safe.cpp
        src/main/cpp/SafeCheck.cpp
        src/main/cpp/exception/ExceptionTools.cpp
        src/main/cpp/tools/DeviceInfo.h
        src/main/cpp/AntiDebug.cpp)

find_library(
            AntiSafe
            log )

target_link_libraries(
        AntiSafe
        android
        log )