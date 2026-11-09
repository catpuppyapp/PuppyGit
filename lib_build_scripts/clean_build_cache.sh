#!/bin/bash
# require cmake and make

# stop if has err
# set -e




# download src and create necessary dirs by `downloadsrc.sh`
export build_root=~/puppylibsbuild


export build_out=$build_root/out
export build_src=$build_root/src
mkdir -p $build_out
mkdir -p $build_src


# set src folder
export opensslsrc=$build_src/openssl
export libssh2src=$build_src/libssh2
export libgit2src=$build_src/libgit2
export git24jsrc=$build_src/git24j
export git24j_c_src=$git24jsrc/src/main/c/git24j


# clean git24j jar
echo "clean git24j jar"
cd $git24jsrc
mvn clean
# clean
rm -rf target
echo "end clean git24j jar"


# set archs
default_archs="x86 x8664 arm32 arm64"
# if no params passed in, build for all archs
archs=${1:-$default_archs}


for arch in $archs; do
    echo "Current architecture: $arch"
    
    if [ "$arch" == "x86" ]; then
        echo "clean for x86..."
        # set in src build folder
        build_out_tmp=build_x86
        #`android-arm`, `android-arm64`, `android-mips`,
        #`android-mip64`, `android-x86`, `android-x86_64` and `android-riscv64`
        #(`*MIPS` targets are no longer supported with NDK R20+)
    elif [ "$arch" == "x8664" ]; then
        echo "clean for x8664..."
        build_out_tmp=build_x8664
    elif [ "$arch" == "arm32" ]; then
        echo "clean for arm32..."
        build_out_tmp=build_arm32
    elif [ "$arch" == "arm64" ]; then
        echo "clean for arm64..."
        build_out_tmp=build_arm64
    fi
   

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

    
done
