import SwiftUI

@available(iOS 16.0, *)
public struct YonaMatchView: View {
    @State private var game = MemoryGame()
    @State private var playing = false
    private let columns = Array(repeating: GridItem(.flexible(), spacing: 10), count: 3)

    public init() {}

    public var body: some View {
        VStack(spacing: 20) {
            Text("🧩 YonaMatch").font(.largeTitle.bold()).foregroundStyle(.teal)
            if playing {
                Text("Deneme: \(game.attempts) · Çift: \(game.matched.count / 2)/3")
                LazyVGrid(columns: columns, spacing: 10) {
                    ForEach(game.cards.indices, id: \.self) { index in
                        Button {
                            game.select(index)
                        } label: {
                            Text(game.revealed.contains(index) || game.matched.contains(index) ? game.cards[index] : "❔")
                                .font(.largeTitle)
                                .frame(maxWidth: .infinity, minHeight: 96)
                                .background(game.matched.contains(index) ? Color.green.opacity(0.25) : Color.orange.opacity(0.25))
                                .clipShape(RoundedRectangle(cornerRadius: 16))
                        }.disabled(game.awaitingReset || game.matched.contains(index))
                    }
                }
                if game.awaitingReset {
                    Button("Tekrar dene") { game.hideMismatch() }
                }
                if game.isComplete {
                    Text("🎉 Harika!").font(.title2.bold())
                    Button("Yeniden Oyna") { game = MemoryGame() }
                }
                Button("Ana Sayfa") { playing = false; game = MemoryGame() }
            } else {
                Text("Eşleştir, keşfet, eğlen!")
                Button("Hafıza Kartlarını Oyna") { playing = true }
                    .buttonStyle(.borderedProminent)
                ForEach(["Gölgesini Bul", "Şekil Eşleştirme", "Renk Eşleştirme", "Parçaları Birleştir", "İlişki Kurma"], id: \.self) {
                    Text("🔒 \($0) — Yakında").foregroundStyle(.secondary)
                }
            }
            Spacer()
        }
        .padding(20)
        .frame(maxWidth: .infinity)
        .background(Color(red: 1, green: 0.976, blue: 0.941))
    }
}
