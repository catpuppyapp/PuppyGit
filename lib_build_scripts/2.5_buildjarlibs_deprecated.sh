#!/bin/bash
# require cmake and make

# stop if has err
set -e


# SET VARIABLE FIRST!
# before run this script block, start: you must set below vars to expect value
# set ndk target abi
export android_target_abi=21

# used for find jar name like: git24j-1.0.3.20241022.jar
export git24j_jar_version="1.0.4.20241114"


# before run this script block, end




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


# set src folder
export opensslsrc=$build_src/openssl
export libssh2src=$build_src/libssh2
export libgit2src=$build_src/libgit2
export git24jsrc=$build_src/git24j
export git24j_c_src=$git24jsrc/src/main/c/git24j




# build x86 libs

# openssl
export ANDROID_NDK_ROOT=$build_root/android-ndk
export ANDROID_TOOLCHAIN_ROOT=$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64
export PATH=$ANDROID_TOOLCHAIN_ROOT/bin:$PATH
export prefix=$ANDROID_TOOLCHAIN_ROOT/sysroot/usr/local


# build git24j jar
echo "start build git24j jar"
cd $git24jsrc
mvn clean compile package "-Dmaven.test.skip=true" "-Dmaven.javadoc.skip=true"

# copy to out dir
cp -f target/git24j-$git24j_jar_version.jar $build_out/
cp -f target/git24j-$git24j_jar_version-sources.jar $build_out/

# clean
rm -rf target

echo "finished"


