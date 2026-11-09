@echo off
REM ./sa_signapk.bat <keystorefile.jks> <outputpath> <unsigned.apk>
apksigner sign --ks %1 --out %2 %3
apksigner verify %2
