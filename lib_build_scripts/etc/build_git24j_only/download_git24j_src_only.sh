#!/bin/bash
# set version and download link, make sure download file is tar.gz, else need edit the uncompress command
GIT24J_VERSION="1.0.4.20241114"




export build_src=~/puppylibsbuild/src
mkdir -p $build_src
cd $build_src

GIT24J_URL="https://github.com/Frank997/git24j/archive/refs/tags/${GIT24J_VERSION}.tar.gz"





# download
echo "Downloading git24j..."
curl -L -o "$build_src/git24j-${GIT24J_VERSION}.tar.gz" "$GIT24J_URL"

echo "Extracting git24j..."
tar -xzf "$build_src/git24j-${GIT24J_VERSION}.tar.gz" -C "$build_src"
mkdir -p git24j
mv git24j-${GIT24J_VERSION}/* git24j


echo "Download and extraction complete."
