# if build git24j failed with "jni_md.h not found", try 3.16.3 or 3.22.1, they are tested
cmake_minimum_required(VERSION 3.31.1)

project(puppygit C)

set(my_lib_path ${CMAKE_SOURCE_DIR}/src/main/jniLibs)
set(my_jni_path ${CMAKE_SOURCE_DIR}/src/main/jni)
set(my_include_path ${my_jni_path}/include)
set(my_c_src_path ${my_jni_path}/src)

# -fvisibility=hidden 不将函数名输出到动态符号表中，但JNIEXPORT修饰的函数仍会输出，因为JNIEXPORT这个宏展开就是设置visibility为default的语句。
add_compile_options(-fvisibility=hidden)

# avoid generate buildid, it'll cause Reproducible Build(RB) verify failed, see: https://gitlab.com/IzzyOnDroid/repo/-/wikis/Reproducible-Builds#no-funny-build-time-generated-ids
add_link_options("-Wl,--build-id=none")


## 要引用的lib
add_library(git2
        SHARED
        IMPORTED
        )

set_target_properties(git2
        PROPERTIES IMPORTED_LOCATION
        ${my_lib_path}/${ANDROID_ABI}/libgit2.so
)

## 要引用的lib
#add_library(git24j
#        SHARED
#        IMPORTED
#        )
#
#set_target_properties(git24j
#        PROPERTIES IMPORTED_LOCATION
#        ${my_lib_path}/${ANDROID_ABI}/libgit24j.so
#)

## 要引用的lib
add_library(crypto
        SHARED
        IMPORTED
        )

set_target_properties(crypto
        PROPERTIES IMPORTED_LOCATION
        ${my_lib_path}/${ANDROID_ABI}/libcrypto.so
)

## 要引用的lib
add_library(ssh2
        SHARED
        IMPORTED
        )

set_target_properties(ssh2
        PROPERTIES IMPORTED_LOCATION
        ${my_lib_path}/${ANDROID_ABI}/libssh2.so
)

## 要引用的lib
add_library(ssl
        SHARED
        IMPORTED
        )

set_target_properties(ssl
        PROPERTIES IMPORTED_LOCATION
        ${my_lib_path}/${ANDROID_ABI}/libssl.so
)

file(GLOB_RECURSE SRC_FILES "${my_c_src_path}/*.c")  # 递归查找所有 .c 文件

#你的c代码
add_library(${CMAKE_PROJECT_NAME}
        SHARED
        ${SRC_FILES}
        )

# android 自带
find_library(LOG-LIB log)

# bhook
find_package(bytehook REQUIRED CONFIG)

# set header files path
#include_directories(${my_include_path})
# or
target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE ${my_include_path})
target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE ${my_jni_path/src/git24j})

target_compile_options(${CMAKE_PROJECT_NAME}
        PRIVATE
        -Wall    #启用所有警告，但并不真是所有
#        -Wextra  #启用附加警告，等同于 -W
#        -Werror  #把警告当错误对待，例如未使用变量，原本只是黄色警告，若开启这个选择，则会变成红色报错编译不通过
        )

target_link_libraries(
        #你的库必须在第一个，否则编译会报错
        ${CMAKE_PROJECT_NAME}
#        git24j
        git2
        ssh2
        crypto
        ssl
        ${LOG-LIB}
        bytehook::bytehook
)
