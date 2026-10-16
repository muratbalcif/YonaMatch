# Faz 02 — İçerik ve kalıcılık sözleşmesi

- Tek kaynak: `shared-content/levels/memory.json`. Paketleme için aynı içerik Android assets ve Swift Package resources içine kopyalanır.
- `schemaVersion=1`, `gameType=memory`, sıralı 20 bölüm, `pairs`, `idealAttempts`.
- Android: `LevelRepository` (assets JSON), `ProgressRepository` (SharedPreferences).
- iOS: `LevelRepository` (Bundle.module JSON), `ProgressRepository` (UserDefaults).
- Tamamlanan seviye: en iyi yıldızın maksimumu ve açılan en ileri bölüm saklanır.
- Her iki tarafta UI navigasyonu şimdilik yerel ekran durumuyla çalışıyor; Navigation Compose/NavigationStack'e geçiş sonraki düzenleme.
- Android ve iOS UI henüz yeni repository katmanını kullanmıyor; bağlantı Faz 03 görevi.
- SharedPreferences ve UserDefaults ilk sürüm için hafif çözüm; çoklu profil ve şema göçü gerektiğinde DataStore / ayrı Codable depolamaya geçilecek.
- Otomatik CI derleme sonucu henüz doğrulanmadı.
