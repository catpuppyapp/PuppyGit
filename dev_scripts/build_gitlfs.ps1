# go to build.gradle.kts find ndkVersion, then find the ndk path on your computer and paste it to here
# e.g. ndkVersion = "26.3.11579264"
$ndkPath = Read-Host -Prompt "NDK path"
$ndkToolchains = "$ndkPath\\toolchains"

# arm64 可禁用cgo，直接不用ndk，无外部依赖，编译出直接在安卓可用的二进制文件
$env:CGO_ENABLED="0"
$env:GOOS="android"
$env:GOARCH="arm64"

# -trimpath二进制文件中避免记录绝对路径
# -buildvcs=false 避免记录git信息，没用
# buildid=空字符串，有助于重复构建生成相同二进制文件，ai说的，未考证
go build -v -trimpath -buildvcs=false -ldflags="-s -w -buildid=" -o bin/git-lfs-android-arm64

echo "arm64 done"

# 以下必须启用cgo

# armv7
$env:CGO_ENABLED = "1"
$env:GOOS = "android"
$env:GOARCH = "arm"
$env:CC = "$ndkToolchains\\llvm\\prebuilt\\windows-x86_64\\bin\\armv7a-linux-androideabi21-clang.cmd"

go build -v -trimpath -buildvcs=false -ldflags="-s -w -buildid=" -o bin/git-lfs-android-arm

echo "arm done"


# x8664
$env:CGO_ENABLED = "1"
$env:GOOS = "android"
$env:GOARCH = "amd64"
$env:CC = "$ndkToolchains\\llvm\\prebuilt\\windows-x86_64\\bin\\x86_64-linux-android21-clang.cmd"

go build -v -trimpath -buildvcs=false -ldflags="-s -w -buildid=" -o bin/git-lfs-android-x8664

echo "x8664 done"


# x86
$env:CGO_ENABLED = "1"
$env:GOOS = "android"
$env:GOARCH = "386"
$env:CC = "$ndkToolchains\\llvm\\prebuilt\\windows-x86_64\\bin\\i686-linux-android21-clang.cmd"

go build -v -trimpath -buildvcs=false -ldflags="-s -w -buildid=" -o bin/git-lfs-android-x86

echo "x86 done"

echo ""

echo "explorer bin"
