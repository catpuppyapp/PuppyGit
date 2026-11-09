#!/bin/bash
# require cmake and make

# usage:
# thisscript "outputdir"

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

build_out=${1:-$build_out}


export build_src=$build_root/src
mkdir -p $build_out
mkdir -p $build_src

export JAVA_HOME=$build_root/jdk


# set src folder
export git24jsrc=$build_src/git24j




# build x86 libs



# build git24j jar
echo "start build git24j jar"
cd $git24jsrc
mvn clean compile package "-Dmaven.test.skip=true" "-Dmaven.javadoc.skip=true"

# copy to out dir
cp -f target/git24j-$git24j_jar_version.jar $build_out/
# cp -f target/git24j-$git24j_jar_version-sources.jar $build_out/

# clean
# rm -rf target

echo "finished"


