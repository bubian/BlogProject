cmake_minimum_required(VERSION 3.4.1)

add_library(
             gifflen
             SHARED
             src/main/cpp/gifflen.cpp
             src/main/cpp/dib.cpp)

find_library(
              log-lib
              log )

target_link_libraries(
                       gifflen
                       ${log-lib} )
include_directories(src/main/cpp/include/dib.h
                    src/main/cpp/include/neuquant.h)