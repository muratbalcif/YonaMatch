# YonaMatch — Ürün Gereksinimleri (PRD)
Sürüm: 0.1 — Faz 00 | Durum: başlangıç kararları, testlerle doğrulanacak

## Vizyon
3–8 yaş çocukların dikkat, hafıza, görsel ayırt etme ve ilişki kurma becerilerini destekleyen; reklamsız oyun ekranları, düşük baskı ve sesli yönlendirmeler sunan eğitici eşleştirme koleksiyonu.

## Hedef kitle
- 3–4: büyük dokunma alanları, az nesne, bol görsel/sesli yardım.
- 5–6: orta zorluk, sürükle-bırak ve eşleştirme çeşitliliği.
- 7–8: daha çok nesne, benzer görseller, daha karmaşık ilişkiler.
Yaş grubu varsayılan zorluğu etkiler; ebeveyn değiştirebilir. Çocuk kişisel bilgileri toplanmaz.

## V1 kapsamı
- Android native Kotlin/Compose; iOS native Swift/SwiftUI.
- 6 oyun türü, her biri 20 seviye (toplam 120).
- TR/EN metin ve sesli yönerge altyapısı.
- Yerel ilerleme: tamamlanan seviyeler, en yüksek yıldız, rozetler, tercihler.
- Çevrimdışı çalışma; hesap, sunucu, reklam SDK'sı ve çocuk davranış analitiği yok.
- Ebeveyn alanı: ses, dil, zorluk, gizlilik, satın alma/geri yükleme (satın alma sonraki faz).
- Erişilebilirlik: en az 48dp/44pt hedefler, şekil+etiket desteği, yalnızca renge dayanmayan geri bildirim.

## Ekranlar
1. Açılış
2. İlk kurulum (dil, ses; atlanabilir)
3. Avatar/profil (kişisel veri istemeyen yerel avatar)
4. Ana sayfa (6 oyun)
5. Seviye haritası
6. Oyun ekranı
7. Bölüm sonucu
8. Rozet/ödül koleksiyonu
9. Ebeveyn alanı (ebeveyn kapısıyla)

## Tasarım ilkeleri
- Açık krem zemin, pastel turkuaz/mercan/sarı/lavanta; büyük, yuvarlatılmış öğeler.
- Okumaya bağımlı olmayan ikonlar ve sesli talimatlar.
- Yanlış cevap: nazik görsel/sesli geri bildirim; ceza yok.
- Çocuğu uygulamada tutmak için manipülatif sayaç, seri/ceza ve baskı yok.
- Animasyonlar kısa ve azaltılabilir; ses kapatılabilir.

## V1 dışı
Bulut senkronizasyonu, çok oyunculu mod, sohbet, kullanıcı hesabı, reklamlar, günlük zorunlu giriş ve çevrimiçi liderlik tablosu.

## Başarı ölçütleri (yayın kabulü)
- Her iki platformda 6 motor, 120 seviye, offline açılış.
- İlerleme uygulama yeniden açılınca korunur.
- Her seviye tamamlanabilir; içerik doğrulama otomatik testlerle yapılır.
- Dokunma, ekran okuyucu, küçük telefon ve tablet kontrolleri.
- Ebeveyn kapısı dış bağlantı/satın alma önünde.
- Mağaza çocuk kategorisi ve gizlilik kuralları yayın öncesinde ayrıca doğrulanır.
