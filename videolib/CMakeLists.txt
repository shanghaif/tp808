# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

#define cpp source path
set(SRC_DIR src/main/cpp/libvideo)
#set *.h source path
include_directories(src/main/cpp/libvideo)


#set cpp source path
aux_source_directory(src/main/cpp/libvideo SRC_LIST)

#set *.so files output path，please add this before add_library
#set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})

add_library( # Sets the name of the library.
        video-utils

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/video-utils.cpp
        ${SRC_LIST})

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        video-utils

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})

#cmake_minimum_required(VERSION 3.4.1)
#include_directories(src/main/cpp/libyuv/include)
#add_subdirectory(src/main/cpp/libyuv ./build)
#aux_source_directory(src/main/cpp SRC_FILE)
#add_library(yuvutil SHARED ${SRC_FILE})
#find_library(log-lib log)
#target_link_libraries(yuvutil ${log-lib} yuv)