#!/bin/bash
# require cmake and make

# usage:
# this_script "outputdir" "archs"

# stop if has err
set -e


# SET VARIABLE FIRST!
# before run this script block, start: you must set below vars to expect value
# set ndk target abi
export android_target_abi=21

# used for find jar name like: git24j-1.0.3.20241022.jar
#export git24j_jar_version="1.0.4.20241114"

# if all build success, will copy libs to here
export build_success_out=/vagrant/build_out

# before run this script block, end

build_success_out=${1:-$build_success_out}




# download src and create necessary dirs by `downloadsrc.sh`
export build_root=$HOME/puppylibsbuild


export build_out=$build_root/out
export build_src=$build_root/src
mkdir -p $build_out
mkdir -p $build_src

export JAVA_HOME=$build_root/jdk

export android_abi_arm64=arm64-v8a
export android_abi_x8664=x86_64
export android_abi_arm32=armeabi-v7a
export android_abi_x86=x86

export arm64inst=$build_out/$android_abi_arm64
export x8664inst=$build_out/$android_abi_x8664
export arm32inst=$build_out/$android_abi_arm32
export x86inst=$build_out/$android_abi_x86

mkdir -p $arm64inst
mkdir -p $x8664inst
mkdir -p $arm32inst
mkdir -p $x86inst

export arm64_toolchain_file=$build_root/libgit2-arm64-toolchain.cmake
export x8664_toolchain_file=$build_root/libgit2-x8664-toolchain.cmake
export arm32_toolchain_file=$build_root/libgit2-armv7-toolchain.cmake
export x86_toolchain_file=$build_root/libgit2-x86-toolchain.cmake

cp -f libgit2-arm64-toolchain.cmake $arm64_toolchain_file
cp -f libgit2-x8664-toolchain.cmake $x8664_toolchain_file
cp -f libgit2-armv7-toolchain.cmake $arm32_toolchain_file
cp -f libgit2-x86-toolchain.cmake $x86_toolchain_file


# set src folder
export opensslsrc=$build_src/openssl
export libssh2src=$build_src/libssh2
export libgit2src=$build_src/libgit2
export openssl_cmake=$build_src/openssl-cmake

# moved to `2_downloadsrc.sh`
#cd $libgit2src
## replace the c standard, else maybe got err
#find . -name 'CMakeLists.txt' -exec sed -i 's|C_STANDARD 90|C_STANDARD 99|' {} \;






# ANDROID_HOME is android sdk root, is sdk root, not ndk root
export ANDROID_HOME=/opt/sdk
export CMAKE_VERSION=3.31.1
export CMAKE_PATH=$ANDROID_HOME/cmake/$CMAKE_VERSION/bin/cmake
$CMAKE_PATH --version
export NDK_VERSION=26.3.11579264
export ANDROID_NDK_ROOT=$ANDROID_HOME/ndk/$NDK_VERSION
export ANDROID_TOOLCHAIN_ROOT=$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64
export PATH=$ANDROID_TOOLCHAIN_ROOT/bin:$PATH
export prefix=$ANDROID_TOOLCHAIN_ROOT/sysroot/usr/local



# set archs
default_archs="x86 x8664 arm32 arm64"
# if no params passed in, build for all archs
archs=${2:-$default_archs}

# set a constant time for make openssl reproducible, see: https://reproducible-builds.org/docs/timestamps/
# openssl似乎会使用时间戳来干点什么导致输出的so文件hash不同，设这个常量可使openssl或其他依赖时间戳且兼容此环境变量的库输出相同的二进制文件
# 单位是秒
export SOURCE_DATE_EPOCH=1734082588

for arch in $archs; do
    echo "Current architecture: $arch"

    if [ "$arch" == "x86" ]; then
        echo "build libs for x86..."
        liboutdir=$x86inst
        toolchainfile=$x86_toolchain_file
        # set in src build folder
        build_out_tmp=build_x86
        #`android-arm`, `android-arm64`, `android-mips`,
        #`android-mip64`, `android-x86`, `android-x86_64` and `android-riscv64`
        #(`*MIPS` targets are no longer supported with NDK R20+)
        opensslarch=android-x86
        cur_android_abi=$android_abi_x86
    elif [ "$arch" == "x8664" ]; then
        echo "build libs for x8664..."
        liboutdir=$x8664inst
        toolchainfile=$x8664_toolchain_file
        build_out_tmp=build_x8664
        opensslarch=android-x86_64
        cur_android_abi=$android_abi_x8664
    elif [ "$arch" == "arm32" ]; then
        echo "build libs for arm32..."
        liboutdir=$arm32inst
        toolchainfile=$arm32_toolchain_file
        build_out_tmp=build_arm32
        opensslarch=android-arm
        cur_android_abi=$android_abi_arm32
    elif [ "$arch" == "arm64" ]; then
        echo "build libs for arm64..."
        liboutdir=$arm64inst
        toolchainfile=$arm64_toolchain_file
        build_out_tmp=build_arm64
        opensslarch=android-arm64
        cur_android_abi=$android_abi_arm64
    fi


    echo "start build openssl"
    cd $openssl_cmake
    # use make, very slow, build 1 target need 8min maybe
    # ./Configure no-tests $opensslarch -D__ANDROID_API__=$android_target_abi --prefix=$prefix --openssldir=$prefix/ssl
    # make install

    # use cmake
    # android patch的作用是使openssl能兼容安卓系统内置证书目录里的证书，好像格式有点旧，所以openssl默认已经不兼容了，这补丁打不打无所谓，我bundled了curl整理的Mozilla证书，不用系统的，系统的太旧
    mkdir -p $build_out_tmp
    cd $build_out_tmp
    # cmake .. --preset android
    $CMAKE_PATH .. -DCMAKE_TOOLCHAIN_FILE=$toolchainfile -DANDROID_ABI=$cur_android_abi -DANDROID_PLATFORM="android-$android_target_abi" -DCMAKE_INSTALL_PREFIX=$prefix -DOPENSSL_TARGET_PLATFORM=$opensslarch -DOPENSSL_CONFIGURE_OPTIONS="-D__ANDROID_API__=$android_target_abi;--openssldir=$prefix/ssl;--prefix=$prefix" -DOPENSSL_INSTALL=TRUE -DOPENSSL_PATCH="$openssl_cmake/patch/android.patch" -DBUILD_SHARED_LIBS=ON -DOPENSSL_SOURCE=$opensslsrc

    $CMAKE_PATH --build . --target install

    cp -f $prefix/lib/libssl.so $liboutdir/libssl.so
    cp -f $prefix/lib/libcrypto.so $liboutdir/libcrypto.so
    # openssl done
    echo "end build openssl"



    echo "start build libssh2"
    # libssh2
    cd $libssh2src
    mkdir -p $build_out_tmp
    cd $build_out_tmp
    $CMAKE_PATH .. -DCMAKE_TOOLCHAIN_FILE=$toolchainfile -DCMAKE_INSTALL_PREFIX=$prefix -DCMAKE_BUILD_TYPE=Release

    $CMAKE_PATH --build . --target install

    cp -f $prefix/lib/libssh2.so $liboutdir/libssh2.so

    echo "end build libssh2"

    #libssh done



    echo "start build libgit2"

    #libgit2
    cd $libgit2src
    mkdir -p $build_out_tmp
    cd $build_out_tmp
    $CMAKE_PATH .. -DCMAKE_TOOLCHAIN_FILE=$toolchainfile -DCMAKE_INSTALL_PREFIX=$prefix -DCMAKE_BUILD_TYPE=Release -DUSE_SSH=ON -DBUILD_TESTS=OFF -DBUILD_CLI=OFF -DBUILD_EXAMPLES=OFF -DBUILD_FUZZERS=OFF -DBUILD_SHARED_LIBS=ON

    $CMAKE_PATH --build . --target install

    cp -f $prefix/lib/libgit2.so $liboutdir/libgit2.so
    # libgit2 done
    echo "end build libgit2"




    echo "all build finished, do clean"
    # clean

    # must clean openssl src folder, else can't build for other architecture
    echo "start clean for openssl src"
    # cd $opensslsrc
    # 用cmake构建执行这个会报错，妈的
    # make clean

    rm -rf $openssl_cmake/$build_out_tmp
    echo "end clean for openssl src"
    
    echo "start clean for libssh2 src"
    rm -rf $libssh2src/$build_out_tmp
    echo "end clean for libssh2 src"
    
    echo "start clean for libgit2 src"
    rm -rf $libgit2src/$build_out_tmp
    echo "end clean for libgit2 src"

    
    echo "all clean done"
    # clean done

    echo "build for '$arch' done, libs output dir: $liboutdir"

    
done

echo "copy all libs to: '$build_success_out'"
mkdir -p $build_success_out
cp -rf $build_out/* $build_success_out
echo "finished"


