cmake_minimum_required(VERSION 3.4.1)

#add_library(
#        AntiSafe
#        SHARED
#        src/main/cpp/safe.cpp
#        src/main/cpp/SafeCheck.cpp
#        src/main/cpp/exception/ExceptionTools.cpp
#        src/main/cpp/exception/antitrace.cpp
#        src/main/cpp/tools/DeviceInfo.h
#        src/main/cpp/firm/*
#        src/main/cpp/AntiDebug.cpp)

add_library(
        AntiSafe
        SHARED
        src/main/cpp/firm/FirmSo.cpp
        src/main/cpp/firm/EncodeSection.cpp)

find_library(
            AntiSafe
            log )

target_link_libraries(
        AntiSafe
        android
        log )