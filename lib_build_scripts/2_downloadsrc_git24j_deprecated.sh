#!/bin/bash
# set version and download link, make sure download file is tar.gz, else need edit the uncompress command
LIBSSH2_VERSION="1.11.1"
OPENSSL_VERSION="3.4.0"
LIBGIT2_VERSION="1.8.4"
GIT24J_VERSION="1.0.4.20241114"




export build_src=~/puppylibsbuild/src
mkdir -p $build_src
cd $build_src

OPENSSL_URL="https://github.com/openssl/openssl/releases/download/openssl-${OPENSSL_VERSION}/openssl-${OPENSSL_VERSION}.tar.gz"
LIBGIT2_URL="https://github.com/libgit2/libgit2/archive/refs/tags/v${LIBGIT2_VERSION}.tar.gz"
GIT24J_URL="https://github.com/Frank997/git24j/archive/refs/tags/${GIT24J_VERSION}.tar.gz"
LIBSSH2_URL="https://github.com/libssh2/libssh2/releases/download/libssh2-${LIBSSH2_VERSION}/libssh2-${LIBSSH2_VERSION}.tar.gz"





# download
echo "Downloading libssh2..."
curl -L -o "${build_src}/libssh2-${LIBSSH2_VERSION}.tar.gz" "$LIBSSH2_URL"

echo "Downloading openssl..."
curl -L -o "$build_src/openssl-${OPENSSL_VERSION}.tar.gz" "$OPENSSL_URL"

echo "Downloading libgit2..."
curl -L -o "$build_src/libgit2-${LIBGIT2_VERSION}.tar.gz" "$LIBGIT2_URL"

echo "Downloading git24j..."
curl -L -o "$build_src/git24j-${GIT24J_VERSION}.tar.gz" "$GIT24J_URL"

# extract
echo "Extracting libssh2..."
tar -xzf "$build_src/libssh2-${LIBSSH2_VERSION}.tar.gz" -C "$build_src"
mkdir -p libssh2
mv libssh2-${LIBSSH2_VERSION}/* libssh2


echo "Extracting openssl..."
tar -xzf "$build_src/openssl-${OPENSSL_VERSION}.tar.gz" -C "$build_src"
mkdir -p openssl
mv openssl-${OPENSSL_VERSION}/* openssl



echo "Extracting git24j..."
tar -xzf "$build_src/git24j-${GIT24J_VERSION}.tar.gz" -C "$build_src"
mkdir -p git24j
mv git24j-${GIT24J_VERSION}/* git24j



echo "Extracting libgit2..."
tar -xzf "$build_src/libgit2-${LIBGIT2_VERSION}.tar.gz" -C "$build_src"
mkdir -p libgit2
mv libgit2-${LIBGIT2_VERSION}/* libgit2

# replace the c standard 90 to c99, else maybe will get err when build the lib
cd libgit2
find . -name 'CMakeLists.txt' -exec sed -i 's|C_STANDARD 90|C_STANDARD 99|' {} \;


# Optional: remove downloaded archive
# rm "$LISSH2_TARGET_DIR/libssh2-$LIBSSH2_VERSION.tar.gz"

echo "Download and extraction complete."
