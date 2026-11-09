#!/bin/bash

#####################
# usage: ./thisscript.sh [repo_path]
# the repo path used for locate the `local.properties` file,
#  this script will append cmake.dir into it,
#  if not specify, will use $GITHUB_WORKSPACE,
#  if $GITHUB_WORKSPACE is empty, will use `/home/runner/work/PuppyGit/PuppyGit`, it is the actually value of $GITHUB_WORKSPACE
#####################

#echo "Updating package list..."
#sudo apt update
#
#echo "Installing: curl cmake make tar libssl-dev maven unzip"
#sudo apt install -y curl cmake make tar libssl-dev maven unzip



export build_root=$HOME/puppylibsbuild
mkdir -p $build_root
cd $build_root

# export ndk_filename="android-ndk-r26d-linux"
# echo "Downloading: ndk"
# curl -L -o "${build_root}/$ndk_filename.zip" "https://dl.google.com/android/repository/${ndk_filename}.zip"
# export ANDROID_NDK_ROOT=$build_root/android-ndk
# mkdir -p $ANDROID_NDK_ROOT
# echo "Extracting: ndk"
# -q : only show msg when err
# unzip -q "$ndk_filename.zip"
# mv android-ndk-r26d/* $ANDROID_NDK_ROOT

echo "Downloading: Android SDK"
# ANDROID_HOME is android sdk root, is sdk root, not ndk root
export TOOLS=$build_root/tools
export ANDROID_CMD_TOOLS=$TOOLS/android-cmdline-tools
export ANDROID_HOME=$build_root/android-sdk
export CMAKE_VERSION=3.31.1
export NDK_VERSION=26.3.11579264
# 创建目标目录以及依赖目录，虽然后面会删一下cmd tools目录，但这个创建依然是有意义的，意义在于创建依赖目录，所以即使后面会删除cmd tools目录，这条命令也依然应该保留
mkdir -p $ANDROID_CMD_TOOLS
mkdir -p $ANDROID_HOME
curl -L -o cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
# 删一下，确保mv后目录结构不变
rm -rf $ANDROID_CMD_TOOLS
# 解压然后移动，确保先删后解压移动，以免当解压后的目录和移动的目标目录同名时误删
unzip cmdline-tools.zip -d $TOOLS/
mv $TOOLS/cmdline-tools $ANDROID_CMD_TOOLS
export ANDROID_SDKMANAGER=$ANDROID_CMD_TOOLS/bin/sdkmanager
chmod +x $ANDROID_SDKMANAGER
echo "install cmake by Android sdkmanager"
yes | $ANDROID_SDKMANAGER --install "cmake;$CMAKE_VERSION" --sdk_root=$ANDROID_HOME
# $ANDROID_HOME/cmdline-tools/bin/sdkmanager --list --sdk_root=$ANDROID_HOME
# cmake root dir
export CMAKE_DIR=$ANDROID_HOME/cmake/$CMAKE_VERSION
# cmake 可执行文件路径
export CMAKE_PATH=$CMAKE_DIR/bin/cmake

echo "print cmake version"
$CMAKE_PATH --version

echo "downloading android ndk..."
# channel 0 代表stable渠道, 1 beta, 3 canary, see: https://github.com/android/ndk-samples/wiki/Configure-NDK-Path#the-sdkmanager-command-line-tool
yes | $ANDROID_SDKMANAGER --channel=0 --install "ndk;$NDK_VERSION" --sdk_root=$ANDROID_HOME

echo "set sdk.dir to local.properties for gradle"
# 设置 sdk.dir 以使 github workflow 使用gradle构建apk的时候能找到我们指定版本的cmake和ndk
# -e是为了能输出换行符\n
# 由于izzydroid 用的gitlab，里面没有$GITHUB_WORKSPACE这个变量，所以用实际变量值替换了，不然路径找不到，会有bug
# echo -e "\ncmake.dir=$CMAKE_DIR" >> $GITHUB_WORKSPACE/local.properties
# if specified repo path, use it, else try use $GITHUB_WORKSPACE, if it doesn't exist, will use a literal path
REPO_PATH=${1:-$GITHUB_WORKSPACE}
REPO_PATH=${REPO_PATH:-/home/runner/work/PuppyGit/PuppyGit}
LOCAL_PROPERTIES_PATH=$REPO_PATH/local.properties
echo -e "\nsdk.dir=$ANDROID_HOME" >> $LOCAL_PROPERTIES_PATH
echo "local.properties at: $LOCAL_PROPERTIES_PATH"
echo "cat local.properties:"
cat $LOCAL_PROPERTIES_PATH


echo "Installation complete"
