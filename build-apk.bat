@echo off
setlocal
where gradle >nul 2>nul
if %errorlevel% neq 0 (
  echo Gradle belum terpasang. Gunakan Android Studio atau jalankan build melalui GitHub Actions.
  pause
  exit /b 1
)
gradle assembleDebug
if %errorlevel% neq 0 exit /b %errorlevel%
echo.
echo APK: app\build\outputs\apk\debug\app-debug.apk
pause
