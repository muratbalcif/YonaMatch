# Seviye tasarımı ve içerik sözleşmesi

## Seviye planı
6 oyun x 20 seviye = 120 seviye.
Her türde seviyeler 1–5 başlangıç, 6–10 kolay, 11–15 orta, 16–20 ileri.
Örnek çift sayıları (oyun türüne göre değişebilir):
- 1–5: 2–3 çift
- 6–10: 4–5 çift
- 11–15: 6–8 çift
- 16–20: 8–12 çift
Küçük ekranlarda kaydırma ve uygun kart boyutları zorunlu.

## Veri şeması (hedef)
```json
{
  "schemaVersion": 1,
  "gameType": "memory",
  "levels": [
    {
      "id": "memory-001",
      "order": 1,
      "difficulty": "beginner",
      "theme": "animals",
      "pairs": [
        {"id": "cat", "leftAsset": "animal_cat", "rightAsset": "animal_cat"},
        {"id": "dog", "leftAsset": "animal_dog", "rightAsset": "animal_dog"}
      ],
      "idealAttempts": 2,
      "hintPolicy": "highlight_pair"
    }
  ]
}
```
Mevcut `shared-content/levels/memory.json` bir önceki prototip formatındadır; Faz 03'te bu sözleşmeye taşınacaktır.

## Doğrulama
- `id` global benzersiz, sıra 1–20, oyun türü geçerli.
- Eşleştirme kimlikleri benzersiz; her eşin sol/sağ varlığı mevcut.
- `idealAttempts >= pairs.length`.
- Her seviye çözülebilir ve birden fazla muğlak doğru eşleşme içermez.
- Görseller çocuklara uygun, lisanslı/özgün ve erişilebilir açıklamalı.
- TR/EN metin/ses karşılıkları mevcut.
- İçerik kod değişmeden genişletilebilir.

## Kalıcı ilerleme sözleşmesi
```json
{
  "schemaVersion": 1,
  "levels": {"memory-001": {"completed": true, "bestStars": 3}},
  "settings": {"language": "tr", "soundEnabled": true, "reducedMotion": false}
}
```
İlerleme güncellemeleri atomik yazılmalı ve eski kayıtlar için sürüm göçü düşünülmeli.
