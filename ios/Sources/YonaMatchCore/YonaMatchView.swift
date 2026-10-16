import SwiftUI

@available(iOS 16.0, *)
public struct YonaMatchView: View {
    private let levels = (try? LevelRepository.memoryLevels()) ?? []
    private let progress = ProgressRepository()
    @State private var game = MemoryGame()
    @State private var page: Page = .home
    @State private var selected = 0
    @State private var level = 1
    @AppStorage("memory_unlocked") private var unlocked = 1
    private let cream = Color(red: 1, green: 0.976, blue: 0.941)
    private let navy = Color(red: 0.12, green: 0.16, blue: 0.22)
    private let columns = Array(repeating: GridItem(.flexible(), spacing: 10), count: 4)
    private let modes: [(String, String, Color)] = [
        ("Hafıza Kartları", "🦁", .orange), ("Gölgesini Bul", "🐘", .blue),
        ("Şekil Eşleştirme", "🔺", .green), ("Renk Eşleştirme", "🎨", .purple),
        ("Parçaları Birleştir", "🍎", .pink), ("İlişki Kurma", "🐮", .yellow)
    ]
    private enum Page { case home, levels, game, result }

    public init() {}

    public var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 18) {
                HStack {
                    if page != .home {
                        Button("‹ Geri") { page = (page == .game || page == .result) ? .levels : .home }
                    }
                    Spacer()
                    Text("🧩 YonaMatch").font(.title.bold()).foregroundStyle(navy)
                    Spacer()
                }
                switch page {
                case .home:
                    Text("Merhaba, minik kaşif! 👋").font(.title2.bold())
                    Text("Eşleştir, keşfet, eğlen!")
                    VStack(alignment: .leading, spacing: 12) {
                        Text("🦁 Maceraya devam et!").font(.title3.bold())
                        Text("Hafıza Kartları · Bölüm \(unlocked)")
                        Button("Devam Et") { selected = 0; page = .levels }
                            .buttonStyle(.borderedProminent)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(20).background(.mint.opacity(0.25), in: RoundedRectangle(cornerRadius: 22))
                    Text("Oyunlarımız").font(.title2.bold())
                    LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 12) {
                        ForEach(modes.indices, id: \.self) { index in
                            Button {
                                selected = index
                                page = .levels
                            } label: {
                                VStack(spacing: 8) {
                                    Text(modes[index].1).font(.system(size: 44))
                                    Text(modes[index].0).font(.subheadline.bold()).multilineTextAlignment(.center)
                                    if index != 0 { Text("Yakında").font(.caption) }
                                }
                                .frame(maxWidth: .infinity, minHeight: 120)
                                .foregroundStyle(navy)
                                .background(modes[index].2.opacity(0.2), in: RoundedRectangle(cornerRadius: 20))
                            }
                        }
                    }
                case .levels:
                    Text("\(modes[selected].1) \(modes[selected].0)").font(.title2.bold())
                    if selected != 0 {
                        Text("Bu oyun yakında hazır olacak. Şimdilik Hafıza Kartları'nı oynayabilirsin.")
                        Button("Hafıza Kartları") { selected = 0 }
                    } else {
                        Text("Bir bölüm seç")
                        LazyVGrid(columns: columns, spacing: 12) {
                            ForEach(1...20, id: \.self) { n in
                                Button {
                                    level = n
                                    game = makeGame()
                                    page = .game
                                } label: {
                                    Text(n <= unlocked ? "\(n) ⭐" : "🔒")
                                        .frame(maxWidth: .infinity, minHeight: 65)
                                        .background(n <= unlocked ? Color.yellow.opacity(0.3) : Color.gray.opacity(0.14),
                                                    in: RoundedRectangle(cornerRadius: 16))
                                }
                                .disabled(n > unlocked)
                            }
                        }
                    }
                case .game:
                    Text("Hafıza Kartları · Bölüm \(level)").font(.title2.bold())
                    Text("Çiftleri bul! · Deneme: \(game.attempts) · \(game.matched.count / 2)/\(game.cards.count / 2)")
                    LazyVGrid(columns: columns, spacing: 10) {
                        ForEach(game.cards.indices, id: \.self) { index in
                            Button {
                                game.select(index)
                                if game.awaitingReset {
                                    Task { @MainActor in
                                        try? await Task.sleep(nanoseconds: 900_000_000)
                                        game.hideMismatch()
                                    }
                                }
                                if game.isComplete {
                                    let ideal = levels.first(where: { $0.order == level })?.idealAttempts ?? game.cards.count / 2
                                    progress.finish(level: level, stars: game.attempts <= ideal ? 3 : 2)
                                    unlocked = progress.unlocked
                                    page = .result
                                }
                            } label: {
                                Text(game.revealed.contains(index) || game.matched.contains(index) ? game.cards[index] : "❔")
                                    .font(.system(size: 30))
                                    .frame(maxWidth: .infinity, minHeight: 72)
                                    .background(game.matched.contains(index) ? Color.mint.opacity(0.4) : Color.blue.opacity(0.25),
                                                in: RoundedRectangle(cornerRadius: 14))
                            }
                            .disabled(game.awaitingReset || game.matched.contains(index) || game.revealed.contains(index))
                        }
                    }
                    Button("Yeniden Başlat") { game = makeGame() }
                case .result:
                    Text("🎉").font(.system(size: 76)).frame(maxWidth: .infinity)
                    Text("Harika! Bölümü tamamladın!").font(.title.bold()).frame(maxWidth: .infinity)
                    Text(String(repeating: "⭐", count: game.attempts <= (levels.first(where: { $0.order == level })?.idealAttempts ?? 3) ? 3 : 2)).font(.largeTitle).frame(maxWidth: .infinity)
                    Text("Deneme sayısı: \(game.attempts)").frame(maxWidth: .infinity)
                    Button("Sonraki Bölüm") {
                        level = min(level + 1, 20)
                        game = makeGame()
                        page = .game
                    }.buttonStyle(.borderedProminent).frame(maxWidth: .infinity)
                    Button("Seviye Haritası") { page = .levels }.frame(maxWidth: .infinity)
                }
                Spacer(minLength: 20)
            }
            .padding(20)
            .foregroundStyle(navy)
        }
        .background(cream.ignoresSafeArea())
    }

    private func makeGame() -> MemoryGame {
        let count = levels.first(where: { $0.order == level })?.pairs ?? 3
        return MemoryGame(pairs: Array(["🦁", "🐘", "🐰", "🐼", "🐧", "🐱", "🐶", "🐻"].prefix(count)))
    }
}
