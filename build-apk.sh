#!/usr/bin/env bash
set -e
gradle assembleDebug
printf '\nAPK: app/build/outputs/apk/debug/app-debug.apk\n'
