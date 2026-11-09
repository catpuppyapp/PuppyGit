1. install curl and gradle:
sudo apt install curl gradle


2. download the puppygit latest release:
curl -L -o "PuppyGit.tar.gz" "https://github.com/catpuppyapp/PuppyGit/archive/refs/tags/1.0.6.7v43.tar.gz" 


3. unzip it
tar -xzf "PuppyGit.tar.gz" -C .
# rename dir "PuppyGit-version" to "PuppyGit"
mkdir -p PuppyGit
mv "PuppyGit-1.0.6.7v43"/* PuppyGit
rm -rf "PuppyGit-1.0.6.7v43"


4. go the the "lib_build_scripts" folder
cd PuppyGit/lib_build_scripts/


5. run "1_installrequire.sh", it will install necessary tools and download openjdk and the android-ndk


6. run "2_downloadsrc.sh", it will download and uncompress libssh2 libgit2 git24j and openssl src


7. run "2.5_buildjarlibs_fdroid.sh" and specify the lib out dir to PuppyGit jar libs dir, the new built will override the jar under PuppyGit libs:
./2.5_buildjarlibs_fdroid.sh PuppyGit/app/libs


8. run "3_buildlibs_fdroid.sh" and specify the out dir to jni libs dir, after run the script, the libs will overrider the files under jniLibs:
./3_buildlibs_fdroid.sh PuppyGit/app/src/main/jniLibs/


9. almost there, now cd to the PuppyGit dir and set ndk path then run gradle with your signature:
cd PuppyGit
echo "sdk.dir=path/to/ndk" > local.properties
./gradlew assembleRelease -Pandroid.injected.signing.store.file=path/to/your/key_store_file -Pandroid.injected.signing.store.password=password_of_key_store_file -Pandroid.injected.signing.key.alias=your_key_alias -Pandroid.injected.signing.key.password=password_of_your_key_alias

All done, now the app should exist in the output dir: PuppyGit/app/build/outputs/apk/release/app-release.apk
