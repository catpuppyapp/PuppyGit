@echo off

REM usage `.\this_script host:port`
REM this script will install the app then launch it

REM go to src dir
cd ../

REM install
adb -s %1 install app\build\outputs\apk\release\app-release.apk

REM start the app method 1
REM adb -s %1 shell am start -n com.catpuppyapp.puppygit.play.pro/.MainActivity

REM start the app method 2
REM adb -s %1 shell am start -a android.intent.action.MAIN -c android.intent.category.LAUNCHER -n com.catpuppyapp.puppygit.play.pro/.MainActivity

REM start the app method 3
adb -s %1 shell monkey -p com.catpuppyapp.puppygit.play.pro -c android.intent.category.LAUNCHER 1
