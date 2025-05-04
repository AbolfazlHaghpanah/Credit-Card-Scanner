cmake_minimum_required(VERSION 3.18.1)

project("scanner")

add_library(${CMAKE_PROJECT_NAME} SHARED
        native-lib.cpp)

set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/../../../../opencv/sdk/native/jni)

find_package(OpenCV REQUIRED)

find_library(
        jnigraphics-lib
        jnigraphics
)

include_directories(${OpenCV_INCLUDE_DIRS})

target_link_libraries(
        ${CMAKE_PROJECT_NAME}
        ${OpenCV_LIBS}
        ${jnigraphics-lib}
        android
        log
)
