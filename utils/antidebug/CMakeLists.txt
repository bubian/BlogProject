cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             AntiDebug

             SHARED

             src/main/cpp/native-lib.cpp
             src/main/cpp/AntiDebug.cpp)

find_library(
            AntiDebug
            log )

target_link_libraries(
        AntiDebug
        android
        log )