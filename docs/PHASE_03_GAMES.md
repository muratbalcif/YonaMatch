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

## Sonraki iterasyon — silüet varlıkları ve kalite kapısı
- `shared-content/art/silhouettes/` altında lion, elephant, rabbit için SVG silüet kaynakları oluşturuldu.
- Bunlar henüz Android VectorDrawable ve iOS PDF/asset catalog biçimlerine dönüştürülmedi. Mevcut UI hâlâ emoji kullanıyor; gerçek silüet entegrasyonu tamamlandı denemez.
- 20 seviye JSON'u her iki platform paketinde bulunuyor ve repository üzerinden okunuyor.
- Android'de gereksiz yinelenen ilerleme kaydı kaldırıldı.
- Tamamlama koşulları: native silüet rendering, sürükle-bırak gerçek cihaz testi, Android/iOS build PASS, 20 seviyenin her iki platformda oynanması, yeniden başlatma ve geri tuşu regresyon testi.
