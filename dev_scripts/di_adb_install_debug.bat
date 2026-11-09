@echo off
cd ../
adb -s %1 install app\build\outputs\apk\debug\app-debug.apk
adb -s %1 shell monkey -p com.catpuppyapp.puppygit.play.pro -c android.intent.category.LAUNCHER 1
