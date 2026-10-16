import SwiftUI

@available(iOS 16.0, *)
public struct YonaMatchView: View {
    private let levels = (try? LevelRepository.memoryLevels()) ?? []
    private let progress = ProgressRepository()
    private let gameProgress = GameProgressStore()
    @State private var resultStars = 3
    @AppStorage("shape_unlocked") private var shapeUnlocked = 1
    @AppStorage("color_unlocked") private var colorUnlocked = 1
    @AppStorage("halves_unlocked") private var halvesUnlocked = 1
    @AppStorage("association_unlocked") private var associationUnlocked = 1
    @AppStorage("shadow_unlocked") private var shadowUnlocked = 1
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

    @ViewBuilder static func halfArtwork(_ item: MatchItem) -> some View {
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
                    switch selected {
                    case 0:
                        MemoryPlayScreen(level: level) { attempts, usedHint in
                            let ideal = levels.first(where: { $0.order == level })?.pairs ?? 3
                            resultStars = gameProgress.finish("memory", level: level, attempts: attempts, idealAttempts: ideal, hintsUsed: usedHint)
                            unlocked = max(unlocked, min(20, level + 1))
                            page = .result
                        }
                    case 1:
                        ShadowPlayScreen(level: level) { attempts, usedHint in
                            let ideal = ((try? ShadowLevelRepository.levels()) ?? []).first(where: { $0.order == level })?.idealAttempts ?? 3
                            resultStars = gameProgress.finish("shadow", level: level, attempts: attempts, idealAttempts: ideal, hintsUsed: usedHint)
                            shadowUnlocked = max(shadowUnlocked, min(20, level + 1))
                            page = .result
                        }
                    case 2, 3, 4:
                        PairMatchingPlayScreen(mode: selected, level: level) { attempts, usedHint in
                            let type = selected == 2 ? "shape" : (selected == 3 ? "color" : "halves")
                            let ideal = ((try? MatchLevelRepository.levels(type)) ?? []).first(where: { $0.order == level })?.idealAttempts ?? 3
                            resultStars = gameProgress.finish(type, level: level, attempts: attempts, idealAttempts: ideal, hintsUsed: usedHint)
                            let next = min(20, level + 1)
                            if selected == 2 { shapeUnlocked = max(shapeUnlocked, next) }
                            else if selected == 3 { colorUnlocked = max(colorUnlocked, next) }
                            else { halvesUnlocked = max(halvesUnlocked, next) }
                            page = .result
                        }
                    default:
                        AssociationPlayScreen(level: level) { attempts, usedHint in
                            let ideal = ((try? AssociationLevelRepository.levels()) ?? []).first(where: { $0.order == level })?.idealAttempts ?? 3
                            resultStars = gameProgress.finish("association", level: level, attempts: attempts, idealAttempts: ideal, hintsUsed: usedHint)
                            associationUnlocked = max(associationUnlocked, min(20, level + 1))
                            page = .result
                        }
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

}
