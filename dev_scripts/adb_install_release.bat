@echo off
cd ../
adb -s %1 install app\build\outputs\apk\release\app-release.apk