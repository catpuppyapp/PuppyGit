step by step to build the libs and apk:

```bash
# set src path first
export GITHUB_WORKSPACE=path_to_your_PuppyGit_src_cloned_from_git

# build the libs
cd $GITHUB_WORKSPACE/lib_build_scripts
bash 1_installrequire.sh
bash 2_downloadsrc.sh
bash 3_buildlibs.sh ${GITHUB_WORKSPACE}/app/src/main/jniLibs

# build the unsigned apk
cd $GITHUB_WORKSPACE
chmod +x gradlew
./gradlew assembleRelease
```
