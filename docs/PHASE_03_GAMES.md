# Faz 03 — Hafıza ve gölge eşleştirme

## Hafıza
- İki kart açılır, eşit kartlar kalıcı açık kalır.
- Yanlış kartlar yaklaşık 900ms sonra otomatik kapanır.
- Animasyon: kart yüzü değişimi crossfade/scale; kullanıcı ayarlarında hareket azaltma ileride.
- Deneme sayısı ve tamamlanan çiftler takip edilir.

## Gölge (ilk oynanabilir prototip)
- Bir nesne seç, ardından doğru gölge hedefini seç.
- Doğru seçim hedefi tamamlar; yanlış seçimde ceza yok.
- Dokunarak eşleştirme sürükle-bırak için erişilebilir alternatif olarak kalır.
- V1 prototipte hedefler yarı saydam emojiyle temsil edilir; gerçek silüet SVG/vector asset'leri henüz üretilmedi.
- İlk seviyede 3 hayvan; farklı zorluk seviyeleri ve JSON sözleşmesi sonraki iterasyon.

## Doğrulama
- ShadowGame birim testi (iOS) eklendi.
- Android/iOS CI ve gerçek cihaz doğrulaması henüz yapılmadı.
- Faz tamamlanmış sayılmadan gerçek silüetler, ses/haptik, sürükle-bırak ve 20 gölge seviyesi eklenecek.
