#!/bin/bash

#echo "Updating package list..."
#sudo apt update
#
#echo "Installing: curl cmake make tar libssl-dev maven unzip"
#sudo apt install -y curl cmake make tar libssl-dev maven unzip



export build_root=~/puppylibsbuild
mkdir -p $build_root
cd $build_root

echo "Downloading: Android SDK"
# ANDROID_HOME is android sdk root, is sdk root, not ndk root
export ANDROID_HOME=$build_root/android-sdk
export CMAKE_VERSION=3.31.1
mkdir -p $ANDROID_HOME/cmdline-tools
curl -L -o cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip cmdline-tools.zip -d $ANDROID_HOME/
echo "install cmake by Android sdkmanager"
yes | $ANDROID_HOME/cmdline-tools/bin/sdkmanager --install "cmake;$CMAKE_VERSION" --sdk_root=$ANDROID_HOME
$ANDROID_HOME/cmdline-tools/bin/sdkmanager --list --sdk_root=$ANDROID_HOME
# cmake root dir
export CMAKE_DIR=$ANDROID_HOME/cmake/$CMAKE_VERSION
# cmake 可执行文件路径
export CMAKE_PATH=$CMAKE_DIR/bin/cmake
echo "set cmake.dir to local.properties for gradle"
# 设置 cmake.dir 以使 github workflow 使用gradle构建apk的时候能找到我们指定版本的cmake
# -e是为了能输出换行符\n
echo -e "\ncmake.dir=$CMAKE_DIR" >> $GITHUB_WORKSPACE/local.properties
echo "print cmake version"
$CMAKE_PATH --version

echo "Installation complete"
