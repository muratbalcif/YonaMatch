# Faz 04 — Şekil ve Renk Oyunları

## İlk oynanabilir sürüm
- Android Compose ve iOS SwiftUI ekranlarına iki oyun bağlandı.
- Şekiller: daire, kare, üçgen, yıldız, elmas.
- Renkler: kırmızı, mavi, yeşil, sarı, mor.
- Kart seçip hedefe dokunma ve sürükleyip hedefe bırakma desteklenir.
- Yanlış eşleşmede ceza yok, doğru eşleşmeler tamamlanmış sayılır.
- İlk 5 bölüm 3, 6–12 bölümler 4, 13–20 bölümler 5 eşleşme içerir.
- Her oyunun kilit açma ilerlemesi cihazda ayrı saklanır.
- Native oyun mantığı ayrı MatchGame tiplerinde yer alır.
- iOS temel eşleşme birim testleri eklendi.

## Açık işler
- Şekil ve renk seviyeleri ortak JSON üzerinden henüz yüklenmiyor; 20 bölüm aynı varlık kümesini tekrar kullanıyor.
- Erişilebilirlik etiketleri, renk körlüğü desteği ve hareket azaltma ayarı.
- Android ve iOS CI derlemeleri ve gerçek cihaz sürükle-bırak testleri doğrulanmadı.
- Seviye bazlı yıldız kalıcılığı ve doğru deneme sayısına göre puanlama geliştirilmeli.
