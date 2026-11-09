#!/bin/bash
# require cmake and make

# stop if has err
set -e



# used for find jar name like: git24j-1.0.3.20241022.jar
export git24j_jar_version="1.0.4.20241114"
success_out_dir=/vagrant/build_out

# before run this script block, end




# download src and create necessary dirs by `downloadsrc.sh`
export build_root=~/puppylibsbuild


export git24jsrc=$build_root/src/git24j

export JAVA_HOME=$build_root/jdk


# build git24j jar
echo "start build git24j jar"
cd $git24jsrc
mvn clean compile package "-Dmaven.test.skip=true" "-Dmaven.javadoc.skip=true"

# copy jar to out dir
mkdir -p $success_out_dir
cp -f target/git24j-$git24j_jar_version.jar $success_out_dir/
cp -f target/git24j-$git24j_jar_version-sources.jar $success_out_dir/




echo "finshed"


