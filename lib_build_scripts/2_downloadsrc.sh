#!/bin/bash
# set version and download link, make sure download file is tar.gz, else need edit the uncompress command
LIBSSH2_VERSION="1.11.1"
OPENSSL_VERSION="4.0.1"
LIBGIT2_VERSION="1.9.7"




export build_src=$HOME/puppylibsbuild/src
mkdir -p $build_src
cd $build_src

OPENSSL_URL="https://github.com/openssl/openssl/releases/download/openssl-${OPENSSL_VERSION}/openssl-${OPENSSL_VERSION}.tar.gz"
LIBGIT2_URL="https://github.com/libgit2/libgit2/archive/refs/tags/v${LIBGIT2_VERSION}.tar.gz"
LIBSSH2_URL="https://github.com/libssh2/libssh2/releases/download/libssh2-${LIBSSH2_VERSION}/libssh2-${LIBSSH2_VERSION}.tar.gz"
OPENSSL_CMAKE_URL="https://github.com/jimmy-park/openssl-cmake/archive/refs/tags/${OPENSSL_VERSION}.tar.gz"




# download
echo "Downloading libssh2..."
curl -L -o "${build_src}/libssh2-${LIBSSH2_VERSION}.tar.gz" "$LIBSSH2_URL"

echo "Downloading openssl..."
curl -L -o "$build_src/openssl-${OPENSSL_VERSION}.tar.gz" "$OPENSSL_URL"

echo "Downloading libgit2..."
curl -L -o "$build_src/libgit2-${LIBGIT2_VERSION}.tar.gz" "$LIBGIT2_URL"

echo "Downloading openssl cmake..."
curl -L -o "$build_src/openssl-cmake-${OPENSSL_VERSION}.tar.gz" "$OPENSSL_CMAKE_URL"

# extract
echo "Extracting libssh2..."
tar -xzf "$build_src/libssh2-${LIBSSH2_VERSION}.tar.gz" -C "$build_src"
rm -rf libssh2
mv libssh2-${LIBSSH2_VERSION} libssh2


echo "Extracting openssl..."
tar -xzf "$build_src/openssl-${OPENSSL_VERSION}.tar.gz" -C "$build_src"
rm -rf openssl
mv openssl-${OPENSSL_VERSION} openssl


echo "Extracting openssl cmake..."
tar -xzf "$build_src/openssl-cmake-${OPENSSL_VERSION}.tar.gz" -C "$build_src"
rm -rf openssl-cmake
mv openssl-cmake-${OPENSSL_VERSION} openssl-cmake


echo "Extracting libgit2..."
tar -xzf "$build_src/libgit2-${LIBGIT2_VERSION}.tar.gz" -C "$build_src"
rm -rf libgit2
mv libgit2-${LIBGIT2_VERSION} libgit2

# no more replace needed, try other ways to make libgit2 use c99 when build for Android:
# method 1(bad，设置这个变量还要设置别的最后引发连锁错误，openssl构建失败): set `CMAKE_SYSTEM_NAME` to "Android", then the libgit2 will use C99 for android
# method 2(tested, good): set `CMAKE_C_STANDARD` to "C99" when build libgit2, if no set, default is C90。(实际操作很简单，追加"-DCMAKE_C_STANDARD=99"到构建libgit2的cmake命令即可)

# replace the c standard 90 to c99, else maybe will get err when build the lib
# 替换这招从 libgit2 1.9.0 开始不好使了，可能有其他字符指定了C90，后来在源代码搜索了下关键字，发现可以在cmake参数指定编译器版本，而且就写在README里，我以前都没注意到....所以其实从一开始就没必要手动替换！
#cd libgit2
#find . -name 'CMakeLists.txt' -exec sed -i 's|C_STANDARD 90|C_STANDARD 99|' {} \;


# Optional: remove downloaded archive
# rm "$LISSH2_TARGET_DIR/libssh2-$LIBSSH2_VERSION.tar.gz"

echo "Download and extraction complete."
