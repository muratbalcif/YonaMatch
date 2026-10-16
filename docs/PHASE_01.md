# Faz 01 — Tasarım Sistemi ve Ekranlar

## Marka
Ad: YonaMatch. Slogan: Eşleştir, keşfet, eğlen!
Karakter evreni: Yona (aslan, ana rehber), Mimo (fil), Pufi (tavşan), Piko (penguen), Pami (panda). Özgün karakter çizimleri için konsept oluşturuldu; uygulamada şimdilik emoji yer tutucular var. Nihai illüstrasyonlar varlık dosyalarına ayrıştırılacak.

## Renk tokenları
- background `#FFF9F0` (krem)
- ink `#1F2937` (lacivert)
- sky `#4DADEB`
- mint `#5DD3A1`
- sunshine `#FFC857`
- coral `#FF8FA3`
- lavender `#B79CFF`

## Tipografi ve ölçüler
- Hedef font: Nunito Rounded veya uygun lisanslı eşdeğeri. V1 prototip sistem fontu kullanıyor.
- Başlık 26–32sp/pt; kart adı 14–16sp/pt; açıklama 14–18sp/pt.
- Kart köşesi 16–22; etkileşim hedefi Android en az 48dp, iOS en az 44pt.
- Oyun kartları renk yanında simgeyle ayırt edilmeli.

## Navigasyon
Ana sayfa → oyun seçimi (ana sayfada 6 kart) → seviye haritası → oyun → sonuç.
Hazır olmayan oyunlar 'Yakında' olarak işaretlenir; açıldığında açıklama görünür.
Ebeveyn alanı ve ödüller ayrı fazda.

## Bu fazda uygulananlar
- Android Compose: ana sayfa, 6 oyun kartı, seviye haritası, hafıza oyunu, sonuç.
- iOS SwiftUI: aynı ekran akışı.
- Hafıza oyununda otomatik yanlış kart kapatma (yaklaşık 900ms).
- Hafıza bölüm kilidi yerel depolamada (Android SharedPreferences, iOS AppStorage).

## Bilinen sınırlamalar
- Nihai karakter görselleri ve Nunito fontu henüz asset olarak entegre edilmedi.
- Yıldızlar henüz gerçek performans hesabına bağlı değil; sonuç ekranı prototip gösterimidir.
- Seviye JSON motorlara bağlı değil, mevcut hafıza bölümleri çift sayısına göre üretiliyor.
- Çalıştırma/derleme ve cihaz testleri henüz doğrulanmadı.
- Android/iOS kart düzenleri ve ekran okuyucu deneyimi gerçek cihazda gözden geçirilmeli.
