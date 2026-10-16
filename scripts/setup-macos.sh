#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if ! command -v brew >/dev/null; then echo "Homebrew gerekli"; exit 1; fi
if ! command -v xcodegen >/dev/null; then brew install xcodegen; fi
(cd ios && xcodegen generate)
echo "iOS: ios/YonaMatch.xcodeproj hazır."
echo "Android: Android Studio ile android/ klasörünü açın (Gradle 8.9, JDK 17)."
