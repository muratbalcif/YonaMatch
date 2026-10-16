# YonaMatch — Oyun Kuralları v0.1

## Ortak akış
Ana sayfa → oyun türü → seviye → yönerge → oyun → sonuç → sonraki bölüm/yeniden oyna.
Her seviyede `levelId`, `gameType`, `difficulty`, `items`, `matchPairs`, `hintPolicy` bulunur.
Tüm oyunlarda duraklat/ana sayfa, ses ve ipucu erişilebilir olmalı.

## Ortak kurallar
- Hatalı seçimler can veya yıldız kaybettirmez; çocuk yeniden dener.
- Süre limiti yoktur; geçen süre istenirse sadece ebeveyn alanında gösterilebilir.
- 1 yıldız = bölümü tamamlama. 2 yıldız = ipucu kullanmadan tamamlama. 3 yıldız = ipucusuz ve en fazla `idealAttempts` denemeyle tamamlama.
- Deneme: oyun motorunun değerlendirdiği bir eşleştirme girişimi. Doğru ve yanlış girişimler sayılır; geçersiz dokunuş sayılmaz.
- Yıldızlar yalnızca en yüksek sonuç olarak saklanır; yeniden oynamak kazanılmış yıldızı azaltmaz.
- Tamamlanan seviye tekrar oynanabilir; ilk tamamlamadan sonra bir sonraki seviye açılır.
- Her oyun türünde 1. seviye açık; diğerleri sırayla açılır. Diğer oyun türleri baştan erişilebilir.
- İpucu sınırsızdır; ipucu kullanımı 2 ve 3 yıldızı engeller ama ilerlemeyi engellemez.
- Üç ardışık hatadan sonra isteğe bağlı, yargılamayan ipucu önerilir.
- Eşleştirme başarı durumuna ulaştığında etkileşim kilitlenir, kutlama gösterilir, ilerleme kaydedilir.
- Arka plana alınan aktif bölüm V1'de yeniden başlatılabilir; tamamlanan seviyeler kaybolmaz.

## 1. Hafıza Kartları (memory)
Kapalı çift kartlar karıştırılır. Tek dokunuş bir kartı açar. İki kart açılınca karşılaştırılır.
Aynıysa çift kalıcı açık kalır; farklıysa yaklaşık 900 ms sonra otomatik kapanır.
Aynı karta iki kez basma, açık/eşleşmiş karta basma ve kapanma animasyonu sırasında giriş geçersizdir.
Bütün çiftler bulunduğunda bölüm biter.
Başlangıç 2–3 çift; üst seviye en çok 12 çift (ekran boyutuna göre kaydırılabilir).

## 2. Gölgesini Bul (shadow)
Nesneyi doğru silüetine sürükle veya seçerek hedefe dokun.
Silüet konturu ayırt edilebilir olmalı; doğru hedef nesneyi yerine sabitler.
Bütün nesneler eşleşince biter. Hatalı hedef nesneyi başlangıca döndürür.

## 3. Şekil Eşleştirme (shape)
Şekli aynı kontur/yuvayla eşleştir. Döndürme V1 kapsamı dışında.
Benzer şekiller ilerleyen seviyelerde kullanılır. Renk tek ayırt edici unsur olamaz.

## 4. Renk Eşleştirme (color)
Nesneleri aynı renk hedeflere eşleştir. Her hedefte renk adı/simge veya desen desteği olmalı.
Renk körlüğüne uygun modda semboller görünür. Yalnızca renk farkına bağlı puanlama yapılmaz.

## 5. Parçaları Birleştir (halves)
İki parçalı resmin eşini bulup birleştir. Parçalar doğru birleşince tam resim gösterilir.
V1'de iki parça; daha çok parçalı yapboz sonraki sürüm.

## 6. İlişki Kurma (association)
Anlamlı çiftler: hayvan–yuva, anne–yavru, nesne–kullanım vb.
Birden fazla doğru cevap olabilecek belirsiz eşleşmeler içerik doğrulamasında reddedilir.
Çocuk için anlaşılır, yaşa uygun görsel/sesli açıklama verilir.

## Yıldız örnekleri
- 3 yıldız: ipucu yok ve deneme <= idealAttempts
- 2 yıldız: ipucu yok ve deneme > idealAttempts
- 1 yıldız: tamamlandı ve en az bir ipucu kullanıldı
İdeal deneme alt sınırı eşleştirme sayısıdır. Zaman puana katılmaz.

## Hata/geri bildirim
Doğru: kısa pozitif animasyon, isteğe bağlı ses/haptik.
Yanlış: hafif sallanma, nötr ses; olumsuz mesaj/ceza yok.
İpucu: bir eşleşme kısa süre vurgulanır; otomatik eşleştirme yapmaz.

## Güvenlik
Oyun sırasında reklam, harici link, mağaza yönlendirmesi ve satın alma butonu bulunmaz.
Ebeveyn kapısı dışına çocuk verisi, tanımlayıcı veya analitik gönderilmez.
