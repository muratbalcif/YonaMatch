import SwiftUI

@available(iOS 16.0, *)
public struct YonaMatchView: View {
    private let levels = (try? LevelRepository.memoryLevels()) ?? []
    private let progress = ProgressRepository()
    private let gameProgress = GameProgressStore()
    @State private var resultStars = 3
    @State private var game = MemoryGame()

    @AppStorage("shape_unlocked") private var shapeUnlocked = 1
    @AppStorage("color_unlocked") private var colorUnlocked = 1
    @AppStorage("halves_unlocked") private var halvesUnlocked = 1
    @AppStorage("association_unlocked") private var associationUnlocked = 1
    @State private var associationTargets: [String: MatchItem] = [:]
    @State private var wrongMatchTarget: String? = nil
    @State private var wrongMatchToken = UUID()
    @State private var matchGame = MatchGame(items: [.init(id: "circle", symbol: "●", label: "Daire")])
    @State private var matchSelection: String? = nil
    @State private var matchHintUsed = false
    @State private var hintSource: String? = nil
    @State private var matchFeedback = "Kartı ve hedefini eşleştir."
    @AppStorage("shadow_unlocked") private var shadowUnlocked = 1
    @State private var shadow = ShadowGame(items: [
        .init(id: "lion", emoji: "🦁", label: "Aslan"),
        .init(id: "elephant", emoji: "🐘", label: "Fil"),
        .init(id: "rabbit", emoji: "🐰", label: "Tavşan")
    ])
    @State private var selectedShadow: String? = nil
    @State private var shadowFeedback = "Önce hayvanı, sonra gölgesini seç."
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
    private func unlockedLevel(for mode: Int) -> Int {
        switch mode {
        case 1: return shadowUnlocked
        case 2: return shapeUnlocked
        case 3: return colorUnlocked
        case 4: return halvesUnlocked
        case 5: return associationUnlocked
        default: return unlocked
        }
    }
    private func matchColor(_ id: String) -> Color {
        switch id {
        case "red": return Color(red: 0.94, green: 0.27, blue: 0.27)
        case "blue": return Color(red: 0.23, green: 0.51, blue: 0.96)
        case "green": return Color(red: 0.13, green: 0.77, blue: 0.37)
        case "yellow": return Color(red: 0.98, green: 0.8, blue: 0.08)
        case "purple": return Color(red: 0.66, green: 0.33, blue: 0.97)
        default: return .gray
        }
    }
    private static let halfArtIds: Set<String> = ["apple", "butterfly", "fish", "car", "house", "flower", "rocket", "turtle"]

    @ViewBuilder private static func halfArtwork(_ item: MatchItem) -> some View {
        if halfArtIds.contains(item.id) {
            HalfArtworkShape(id: item.id)
                .fill(halfArtworkColor(item.id))
                .frame(width: 56, height: 56)
        } else {
            Text(item.symbol).font(.system(size: 47))
                .frame(width: 56, height: 56)
        }
    }

    private static func halfArtworkColor(_ id: String) -> Color {
        switch id {
        case "apple", "rocket": return Color(red: 0.96, green: 0.42, blue: 0.44)
        case "butterfly": return Color(red: 0.72, green: 0.61, blue: 1)
        case "fish": return Color(red: 0.30, green: 0.68, blue: 0.92)
        case "car", "flower": return Color(red: 1, green: 0.56, blue: 0.64)
        case "house": return Color(red: 1, green: 0.79, blue: 0.34)
        default: return Color(red: 0.36, green: 0.83, blue: 0.63)
        }
    }

    @ViewBuilder private func halfVisual(_ item: MatchItem, left: Bool) -> some View {
        Self.halfArtwork(item)
            .frame(width: 56, height: 62)
            .offset(x: left ? 14 : -14)
            .frame(width: 28, height: 62)
            .clipped()
            .accessibilityLabel(item.label)
    }
    private struct JoiningVisual: View {
        let item: MatchItem
        @State private var joined = false

        var body: some View {
            HStack(spacing: 0) {
                half(left: true).offset(x: joined ? 0 : -14)
                half(left: false).offset(x: joined ? 0 : 14)
            }
            .frame(height: 72)
            .onAppear {
                withAnimation(.easeOut(duration: 0.48)) { joined = true }
            }
            .accessibilityLabel(item.label)
        }

        private func half(left: Bool) -> some View {
            YonaMatchView.halfArtwork(item)
                .frame(width: 56, height: 62)
                .offset(x: left ? 14 : -14)
                .frame(width: 28, height: 62)
                .clipped()
        }
    }

    @ViewBuilder private func matchVisual(_ item: MatchItem, faded: Bool = false) -> some View {
        if selected == 3 {
            Circle().fill(matchColor(item.id).opacity(faded ? 0.25 : 1))
                .frame(width: 42, height: 42)
        } else {
            Text(item.symbol).font(.system(size: 38))
                .foregroundStyle(navy.opacity(faded ? 0.22 : 1))
        }
    }

    private enum Page { case home, levels, game, result }

    public init() {}

    public var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 18) {
                HStack {
                    if page != .home {
                        Button(page == .game && (selected >= 2 && selected <= 5) ? "‹" : "‹ Geri") { page = (page == .game || page == .result) ? .levels : .home }
                    }
                    Spacer()
                    if page != .game || (selected < 2 || selected > 5) { Text("🧩 YonaMatch").font(.title.bold()).foregroundStyle(navy) }
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
                                    if index > 4 { Text("Yakında").font(.caption) }
                                }
                                .frame(maxWidth: .infinity, minHeight: 120)
                                .foregroundStyle(navy)
                                .background(modes[index].2.opacity(0.2), in: RoundedRectangle(cornerRadius: 20))
                            }
                        }
                    }
                case .levels:
                    Text("\(modes[selected].1) \(modes[selected].0)").font(.title2.bold())
                    if selected > 5 {
                        Text("Bu oyun yakında hazır olacak. Şimdilik Hafıza Kartları'nı oynayabilirsin.")
                        Button("Hafıza Kartları") { selected = 0 }
                    } else {
                        Text("Bir bölüm seç")
                        LazyVGrid(columns: columns, spacing: 12) {
                            ForEach(1...20, id: \.self) { n in
                                Button {
                                    level = n
                                    if selected == 1 { resetShadow() } else if selected >= 2 && selected <= 5 { resetMatching() } else { game = makeGame() }
                                    page = .game
                                } label: {
                                    Text(n <= unlockedLevel(for: selected) ? "\(n)" + (gameProgress.bestStars(["memory", "shadow", "shape", "color", "halves", "association"][selected], level: n) > 0 ? " " + String(repeating: "⭐", count: gameProgress.bestStars(["memory", "shadow", "shape", "color", "halves", "association"][selected], level: n)) : "") : "🔒")
                                        .frame(maxWidth: .infinity, minHeight: 65)
                                        .background((n <= (unlockedLevel(for: selected))) ? Color.yellow.opacity(0.3) : Color.gray.opacity(0.14),
                                                    in: RoundedRectangle(cornerRadius: 16))
                                }
                                .disabled(n > (unlockedLevel(for: selected)))
                            }
                        }
                    }
                case .game:
                    if selected >= 2 && selected <= 5 {
                        Button {
                            if let source = matchGame.items.first(where: { !matchGame.matched.contains($0.id) }) {
                                matchHintUsed = true
                                hintSource = source.id
                                matchSelection = source.id
                            }
                        } label: { Text("💡").font(.system(size: 30)) }
                        Text(selected == 2 ? "🔺" : (selected == 3 ? "🎨" : (selected == 4 ? "🧩" : "🔗"))).font(.system(size: 30))
                        HStack(spacing: 8) {
                            ForEach(matchGame.items.filter { !matchGame.matched.contains($0.id) }) { item in
                                Button { matchSelection = item.id } label: {
                                    Group { if selected == 4 { halfVisual(item, left: true) } else { matchVisual(item) } }
                                        .frame(maxWidth: .infinity, minHeight: 84)
                                        .background(matchSelection == item.id ? Color.yellow.opacity(0.5) : Color.mint.opacity(0.2), in: RoundedRectangle(cornerRadius: 14))
                                }
                                .onDrag { NSItemProvider(object: item.id as NSString) }
                            }
                        }
                        HStack(spacing: 8) {
                            ForEach(matchGame.targets) { item in
                                Button {
                                    guard let source = matchSelection else { return }
                                    submitMatching(source: source, target: item.id)
                                } label: {
                                    Group {
                                        if wrongMatchTarget == item.id { Text("↻").font(.system(size: 30)).foregroundStyle(.red) }
                                        else if matchGame.matched.contains(item.id) { Text("✓").font(.system(size: 30)) }
                                        else if selected == 4 { halfVisual(item, left: false) }
                                        else { matchVisual(selected == 5 ? (associationTargets[item.id] ?? item) : item, faded: selected == 2) }
                                    }
                                        .frame(maxWidth: .infinity, minHeight: 84)
                                        .background(matchGame.matched.contains(item.id) ? Color.mint.opacity(0.5) : (wrongMatchTarget == item.id ? Color.red.opacity(0.16) : Color.purple.opacity(0.2)), in: RoundedRectangle(cornerRadius: 14))
                                }
                                .disabled(matchGame.matched.contains(item.id))
                                .onDrop(of: [.plainText], isTargeted: nil) { providers in
                                    guard let provider = providers.first else { return false }
                                    _ = provider.loadObject(ofClass: NSString.self) { value, _ in
                                        guard let source = value as? String else { return }
                                        DispatchQueue.main.async { submitMatching(source: source, target: item.id) }
                                    }
                                    return true
                                }
                            }
                        }
                        if selected == 5 && !matchGame.matched.isEmpty {
                            VStack(alignment: .leading, spacing: 8) {
                                Text("✨").font(.system(size: 28))
                                LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 10), count: 2), spacing: 10) {
                                    ForEach(matchGame.items.filter { matchGame.matched.contains($0.id) }) { solved in
                                        HStack(spacing: 3) {
                                            Text(solved.symbol).font(.system(size: 30))
                                            Text("💚").font(.system(size: 17))
                                            Text(associationTargets[solved.id]?.symbol ?? "").font(.system(size: 30))
                                        }
                                        .frame(maxWidth: .infinity, minHeight: 76)
                                        .background(Color.mint.opacity(0.25), in: RoundedRectangle(cornerRadius: 18))
                                        .transition(.scale.combined(with: .opacity))
                                    }
                                }
                            }
                            .animation(.spring(response: 0.35, dampingFraction: 0.65), value: matchGame.matched.count)
                        }
                        if selected == 4 && !matchGame.matched.isEmpty {
                            VStack(alignment: .leading, spacing: 8) {
                                Text("✨").font(.system(size: 28))
                                LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 10), count: 3), spacing: 10) {
                                    ForEach(matchGame.items.filter { matchGame.matched.contains($0.id) }) { solved in
                                        JoiningVisual(item: solved)
                                            .padding(10)
                                            .background(Color.mint.opacity(0.3), in: RoundedRectangle(cornerRadius: 18))
                                            .transition(.scale.combined(with: .opacity))
                                    }
                                }
                            }
                            .animation(.spring(response: 0.35, dampingFraction: 0.65), value: matchGame.matched.count)
                        }
                        Button("↻") { resetMatching() }.font(.system(size: 30)).accessibilityLabel("Yeniden başlat")
                    } else if selected == 1 {
                        Text("🐘 Gölgesini Bul").font(.title2.bold())
                        Text(shadowFeedback)
                        Text("Hayvanlar").font(.headline)
                        HStack(spacing: 10) {
                            ForEach(shadow.items.filter { !shadow.matched.contains($0.id) }) { item in
                                Button {
                                    selectedShadow = item.id
                                } label: {
                                    Text(item.emoji).font(.system(size: 42))
                                        .frame(maxWidth: .infinity, minHeight: 88)
                                        .background(selectedShadow == item.id ? Color.yellow.opacity(0.6) : Color.blue.opacity(0.2),
                                                    in: RoundedRectangle(cornerRadius: 16))
                                }
                                .onDrag { NSItemProvider(object: item.id as NSString) }
                            }
                        }
                        Text("Gölgesini seç").font(.headline)
                        HStack(spacing: 10) {
                            ForEach(shadow.targets) { item in
                                Button {
                                    guard let source = selectedShadow else { return }
                                    let correct = shadow.match(sourceId: source, targetId: item.id)
                                    shadowFeedback = correct ? "Harika, doğru eşleşme!" : "Tekrar deneyelim!"
                                    selectedShadow = nil
                                    if shadow.isComplete { shadowUnlocked = max(shadowUnlocked, min(20, level + 1)); resultStars = gameProgress.finish("shadow", level: level, attempts: shadow.attempts, idealAttempts: shadow.items.count); page = .result }
                                } label: {
                                    Text(shadow.matched.contains(item.id) ? "✓" : item.emoji)
                                        .font(.system(size: 42))
                                        .opacity(shadow.matched.contains(item.id) ? 1 : 0.25)
                                        .frame(maxWidth: .infinity, minHeight: 88)
                                        .background(Color.purple.opacity(0.2), in: RoundedRectangle(cornerRadius: 16))
                                }
                                .disabled(shadow.matched.contains(item.id))
                                .onDrop(of: [.plainText], isTargeted: nil) { providers in
                                    guard let provider = providers.first else { return false }
                                    _ = provider.loadObject(ofClass: NSString.self) { value, _ in
                                        guard let source = value as? String else { return }
                                        DispatchQueue.main.async {
                                            let correct = shadow.match(sourceId: source, targetId: item.id)
                                            shadowFeedback = correct ? "Harika, doğru eşleşme!" : "Tekrar deneyelim!"
                                            if shadow.isComplete { shadowUnlocked = max(shadowUnlocked, min(20, level + 1)); resultStars = gameProgress.finish("shadow", level: level, attempts: shadow.attempts, idealAttempts: shadow.items.count); page = .result }
                                        }
                                    }
                                    return true
                                }
                            }
                        }
                        Text("Eşleşen: \(shadow.matched.count)/\(shadow.items.count) · Deneme: \(shadow.attempts)")
                        Button("Yeniden Başlat") { resetShadow() }
                    } else {
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
                                    resultStars = gameProgress.finish("memory", level: level, attempts: game.attempts, idealAttempts: ideal)
                                    progress.finish(level: level, stars: resultStars)
                                    unlocked = progress.unlocked
                                    page = .result
                                }
                            } label: {
                                Text(game.revealed.contains(index) || game.matched.contains(index) ? game.cards[index] : "❔")
                                    .contentTransition(.opacity)
                                    .animation(.easeInOut(duration: 0.28), value: game.revealed.contains(index) || game.matched.contains(index))
                                    .font(.system(size: 30))
                                    .frame(maxWidth: .infinity, minHeight: 72)
                                    .background(game.matched.contains(index) ? Color.mint.opacity(0.4) : Color.blue.opacity(0.25),
                                                in: RoundedRectangle(cornerRadius: 14))
                            }
                            .disabled(game.awaitingReset || game.matched.contains(index) || game.revealed.contains(index))
                        }
                    }
                    Button("Yeniden Başlat") { game = makeGame() }
                    }
                case .result:
                    VStack(spacing: 18) {
                        Text("🎊")
                            .font(.system(size: 76))
                            .transition(.scale.combined(with: .opacity))
                        Text(String(repeating: "⭐", count: resultStars))
                            .font(.system(size: 40))
                        Text("🌈  🦁  🎈").font(.system(size: 42))
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 30)
                    .background(Color.yellow.opacity(0.2), in: RoundedRectangle(cornerRadius: 32))
                    HStack(spacing: 12) {
                        Button {
                            if selected == 1 { resetShadow() }
                            else if selected >= 2 && selected <= 5 { resetMatching() }
                            else { game = makeGame() }
                            page = .game
                        } label: {
                            Text("↻").font(.system(size: 32))
                                .frame(maxWidth: .infinity, minHeight: 68)
                        }
                        .buttonStyle(.bordered)
                        Button {
                            if level >= 20 {
                                page = .levels
                            } else {
                                level += 1
                                if selected == 1 { resetShadow() }
                                else if selected >= 2 && selected <= 5 { resetMatching() }
                                else { game = makeGame() }
                                page = .game
                            }
                        } label: {
                            Text(level == 20 ? "🏠" : "▶").font(.system(size: 34))
                                .frame(maxWidth: .infinity, minHeight: 68)
                        }
                        .buttonStyle(.borderedProminent)
                    }
                    Button { page = .levels } label: {
                        Text("▦").font(.system(size: 28))
                            .frame(maxWidth: .infinity, minHeight: 56)
                    }
                    .buttonStyle(.bordered)

                }
                Spacer(minLength: 20)
            }
            .padding(20)
            .foregroundStyle(navy)
        }
        .background(cream.ignoresSafeArea())
    }

    private func resetMatching() {
        let items: [MatchItem]
        if selected == 5 {
            let pairs = ((try? AssociationLevelRepository.levels()) ?? []).first(where: { $0.order == level })?.pairs ?? []
            associationTargets = Dictionary(uniqueKeysWithValues: pairs.map { ($0.source.id, $0.target) })
            items = pairs.map { $0.source }
        } else {
            let type = selected == 2 ? "shape" : (selected == 3 ? "color" : "halves")
            items = (try? MatchLevelRepository.items(type, level: level)) ?? []
            associationTargets = [:]
        }
        if !items.isEmpty { matchGame = MatchGame(items: items) }
        matchSelection = nil
        matchHintUsed = false
        hintSource = nil
        wrongMatchTarget = nil
        matchFeedback = "Kartı ve hedefini eşleştir."
    }

    private func submitMatching(source: String, target: String) {
        let correct = matchGame.match(source: source, target: target)
        matchFeedback = correct ? "Harika!" : "Tekrar deneyelim!"
        if correct {
            wrongMatchTarget = nil
        } else {
            wrongMatchTarget = target
            let token = UUID()
            wrongMatchToken = token
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.65) {
                if wrongMatchToken == token { wrongMatchTarget = nil }
            }
        }
        matchSelection = nil
        hintSource = nil
        if matchGame.complete {
            let type = selected == 2 ? "shape" : (selected == 3 ? "color" : (selected == 4 ? "halves" : "association"))
            let ideal: Int
            if selected == 5 {
                ideal = ((try? AssociationLevelRepository.levels()) ?? []).first(where: { $0.order == level })?.idealAttempts ?? matchGame.items.count
            } else {
                ideal = ((try? MatchLevelRepository.levels(type)) ?? []).first(where: { $0.order == level })?.idealAttempts ?? matchGame.items.count
            }
            resultStars = gameProgress.finish(type, level: level, attempts: matchGame.attempts, idealAttempts: ideal, hintsUsed: matchHintUsed)
            if selected == 2 { shapeUnlocked = max(shapeUnlocked, min(20, level + 1)) }
            else if selected == 3 { colorUnlocked = max(colorUnlocked, min(20, level + 1)) }
            else if selected == 4 { halvesUnlocked = max(halvesUnlocked, min(20, level + 1)) }
            else if selected == 5 { associationUnlocked = max(associationUnlocked, min(20, level + 1)) }
            page = .result
        }
    }

    private func resetShadow() {
        shadow = ShadowGame(items: (try? ShadowLevelRepository.items(for: level)) ?? [])
        selectedShadow = nil
        shadowFeedback = "Önce hayvanı, sonra gölgesini seç."
    }

    private func makeGame() -> MemoryGame {
        let count = levels.first(where: { $0.order == level })?.pairs ?? 3
        return MemoryGame(pairs: Array(["🦁", "🐘", "🐰", "🐼", "🐧", "🐱", "🐶", "🐻"].prefix(count)))
    }
}
