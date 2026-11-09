@echo off
cd ../
./gradlew assembleDebug -Pandroid.injected.signing.store.file=%USERPROFILE%/.myandroid/mydebugkey-android.jks -Pandroid.injected.signing.store.password=android -Pandroid.injected.signing.key.alias=android -Pandroid.injected.signing.key.password=android

pause
