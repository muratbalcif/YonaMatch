# Faz 02 — Native hedefler ve CI

## Android
- Kotlin 2.0.21, Compose, AGP 8.7.3, Gradle 8.9, JDK 17
- Android Studio: `android/` aç, Gradle 8.9 seç, `:app:assembleDebug` çalıştır.
- CI, Gradle 8.9'u otomatik kurar. Wrapper jar henüz repoya eklenmedi; ileride `gradle wrapper --gradle-version 8.9` ile eklenmeli.

## iOS
- Swift Package `ios/Package.swift` altında oyun mantığını barındırır.
- XcodeGen `ios/project.yml` ile gerçek iOS app target üretir.
- Mac: `brew install xcodegen`, ardından `cd ios && xcodegen generate`.
- `ios/YonaMatch.xcodeproj` dosyasını Xcode'da aç.
- `YonaMatch` scheme'ini simülatörde çalıştır.
- `swift test` ile temel oyun testlerini çalıştır.
- App Store için Signing & Capabilities altında Apple Development Team seçilmeli.

## CI
- `.github/workflows/android.yml`: Android debug APK derlemesi.
- `.github/workflows/ios.yml`: iOS simülatör derlemesi ve Swift Package testleri.

## Henüz tamamlanmayanlar
- CI koşularının sonuçları doğrulanmadı.
- Android Gradle wrapper jar henüz yok.
- Oyun seviye JSON'u UI'ya bağlanmadı.
- Erişilebilirlik, otomatik kart kapatma, yıldız/ilerleme Faz 03/06 kapsamında.
