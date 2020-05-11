cmake_minimum_required(VERSION 3.4.1)


include_directories(src/main/cpp/include)

file(GLOB my_source_path  src/main/cpp/*.cpp )

add_library(
             Imgprocess
             SHARED
             ${my_source_path} )

add_library( lib_opencv SHARED IMPORTED)
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_java3.so)

find_library(
              log-lib
              log )

find_library(jnigraphics-lib
        jnigraphics )

target_link_libraries(
                       Imgprocess
                       ${jnigraphics-lib}
                       ${log-lib}
                       lib_opencv)