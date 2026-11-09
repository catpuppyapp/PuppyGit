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
export build_root=~/puppylibsbuild


export build_out=$build_root/out
export build_src=$build_root/src
mkdir -p $build_out
mkdir -p $build_src

export JAVA_HOME=$build_root/jdk

export arm64inst=$build_out/arm64-v8a
export x8664inst=$build_out/x86_64
export arm32inst=$build_out/armeabi-v7a
export x86inst=$build_out/x86

mkdir -p $arm64inst
mkdir -p $x8664inst
mkdir -p $arm32inst
mkdir -p $x86inst

export arm64_toolchain_file=$build_root/libgit2-arm64-toolchain.cmake
export x8664_toolchain_file=$build_root/libgit2-x8664-toolchain.cmake
export arm32_toolchain_file=$build_root/libgit2-armv7-toolchain.cmake
export x86_toolchain_file=$build_root/libgit2-x86-toolchain.cmake

cp libgit2-arm64-toolchain.cmake $arm64_toolchain_file
cp libgit2-x8664-toolchain.cmake $x8664_toolchain_file
cp libgit2-armv7-toolchain.cmake $arm32_toolchain_file
cp libgit2-x86-toolchain.cmake $x86_toolchain_file


# set src folder
export opensslsrc=$build_src/openssl
export libssh2src=$build_src/libssh2
export libgit2src=$build_src/libgit2
export git24jsrc=$build_src/git24j
export git24j_c_src=$git24jsrc/src/main/c/git24j

# moved to `2_downloadsrc.sh`
#cd $libgit2src
## replace the c standard, else maybe got err
#find . -name 'CMakeLists.txt' -exec sed -i 's|C_STANDARD 90|C_STANDARD 99|' {} \;



# build x86 libs

# openssl
export ANDROID_NDK_ROOT=$build_root/android-ndk
export ANDROID_TOOLCHAIN_ROOT=$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64
export PATH=$ANDROID_TOOLCHAIN_ROOT/bin:$PATH
export prefix=$ANDROID_TOOLCHAIN_ROOT/sysroot/usr/local

# moved to `2.5_buildjarlibs.sh`
## build git24j jar
#echo "start build git24j jar"
#cd $git24jsrc
#mvn clean compile package "-Dmaven.test.skip=true"
#cp target/git24j-$git24j_jar_version.jar $build_out/git24j-$git24j_jar_version.jar
## clean
#rm -rf target
#echo "end build git24j jar"


# set archs
default_archs="x86 x8664 arm32 arm64"
# if no params passed in, build for all archs
archs=${2:-$default_archs}



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
    elif [ "$arch" == "x8664" ]; then
        echo "build libs for x8664..."
        liboutdir=$x8664inst
        toolchainfile=$x8664_toolchain_file
        build_out_tmp=build_x8664
        opensslarch=android-x86_64
    elif [ "$arch" == "arm32" ]; then
        echo "build libs for arm32..."
        liboutdir=$arm32inst
        toolchainfile=$arm32_toolchain_file
        build_out_tmp=build_arm32
        opensslarch=android-arm
        
    elif [ "$arch" == "arm64" ]; then
        echo "build libs for arm64..."
        liboutdir=$arm64inst
        toolchainfile=$arm64_toolchain_file
        build_out_tmp=build_arm64
        opensslarch=android-arm64
    fi
    
        
    echo "start build opessl"
    cd $opensslsrc
    ./Configure no-tests $opensslarch -D__ANDROID_API__=$android_target_abi --prefix=$prefix --openssldir=$prefix/ssl

    make install

    cp $prefix/lib/libssl.so $liboutdir/libssl.so
    cp $prefix/lib/libcrypto.so $liboutdir/libcrypto.so
    # openssl done
    echo "end build opessl"

    echo "start build libssh2"
    # libssh2
    cd $libssh2src
    mkdir -p $build_out_tmp
    cd $build_out_tmp
    cmake .. -DCMAKE_TOOLCHAIN_FILE=$toolchainfile -DCMAKE_INSTALL_PREFIX=$prefix -DCMAKE_BUILD_TYPE=Release

    cmake --build . --target install

    cp $prefix/lib/libssh2.so $liboutdir/libssh2.so

    echo "end build libssh2"

    #libssh done



    echo "start build libgit2"

    #libgit2
    cd $libgit2src
    mkdir -p $build_out_tmp
    cd $build_out_tmp
    cmake .. -DCMAKE_TOOLCHAIN_FILE=$toolchainfile -DCMAKE_INSTALL_PREFIX=$prefix -DCMAKE_BUILD_TYPE=Release -DUSE_SSH=ON -DBUILD_TESTS=OFF -DBUILD_CLI=OFF -DBUILD_EXAMPLES=OFF -DBUILD_FUZZERS=OFF -DBUILD_SHARED_LIBS=ON

    cmake --build . --target install

    cp $prefix/lib/libgit2.so $liboutdir/libgit2.so
    # libgit2 done
    echo "end build libgit2"


    echo "start build git24j"

    # git24j c lib
    cd $git24j_c_src
    mkdir -p $build_out_tmp
    cd $build_out_tmp
    # should use cmake 3.16.3 or 3.22.1, if newer, may can't found the jni_md.h even I set include path on the command, 一开始以为是cmaketoolchain限制使用外部目录的库，但我尝试把jdk放到ndk sysroot目录下再设置环境变量也没用，不知道原因，算了，放弃
    cmake .. -DCMAKE_TOOLCHAIN_FILE=$toolchainfile -DCMAKE_INSTALL_PREFIX=$liboutdir -DCMAKE_BUILD_TYPE=Release -DJAVA_INCLUDE_PATH2=$JAVA_HOME/include/linux -DJAVA_AWT_INCLUDE_PATH=$JAVA_HOME/include -DJAVA_AWT_LIBRARY=$JAVA_HOME/jre/lib/amd64/libawt.so -DJAVA_JVM_LIBRARY=$JAVA_HOME/jre/lib/amd64/server/libjvm.so -DINCLUDE_LIBGIT2=$libgit2src/include -DLINK_LIBGIT2=$liboutdir
    cmake --build .

    cp libgit24j.so $liboutdir/libgit24j.so
    # git24j done

    echo "end build git24j"



    echo "all build finshed, do clean"
    # clean

    # must clean openssl src folder, else can't build for other architecture
    echo "start clean for openssl src"
    cd $opensslsrc
    make clean
    echo "end clean for openssl src"
    
    echo "start clean for libssh2 src"
    rm -rf $libssh2src/$build_out_tmp
    echo "end clean for libssh2 src"
    
    echo "start clean for libgit2 src"
    rm -rf $libgit2src/$build_out_tmp
    echo "end clean for libgit2 src"
    
    echo "start clean for git24j src"
    rm -rf $git24j_c_src/$build_out_tmp
    echo "end clean for git24j src"
    
    
    echo "all clean done"
    # clean done

    echo "build for '$arch' done, libs output dir: $liboutdir"

    
done

echo "copy all libs to: '$build_success_out'"
mkdir -p $build_success_out
cp -rf $build_out/* $build_success_out
echo "finshed"


