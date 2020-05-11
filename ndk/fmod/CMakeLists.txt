cmake_minimum_required(VERSION 3.4.1)

find_library( log-lib
              log )

set(distribution_DIR ../../../../libs)

add_library( fmod
             SHARED
             IMPORTED )
set_target_properties( fmod
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/${ANDROID_ABI}/libfmod.so )

add_library( fmodL
             SHARED
             IMPORTED )
set_target_properties( fmodL
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/${ANDROID_ABI}/libfmodL.so )

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

add_library( change-voice
             SHARED
             src/main/cpp/change-voice.cc
             src/main/cpp/voice/common_platform.cpp)

include_directories(src/main/cpp/voice)

target_link_libraries( change-voice
                       fmod
                       fmodL
                       ${log-lib})

