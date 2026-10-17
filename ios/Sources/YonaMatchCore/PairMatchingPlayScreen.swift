import SwiftUI

/// Separate state and visual lifecycle for shape, color and halves modes.
@available(iOS 16.0, *)
struct PairMatchingPlayScreen: View {
    let mode: Int
    let level: Int
    let onComplete: (Int, Bool) -> Void
    @State private var game: MatchGame
    @State private var selectedSource: String?
    @State private var selectedRight: String?
    @State private var hintUsed = false
    @State private var wrongTarget: String?

    init(mode: Int, level: Int, onComplete: @escaping (Int, Bool) -> Void) {
        self.mode = mode
        self.level = level
        self.onComplete = onComplete
        let type = mode == 2 ? "shape" : (mode == 3 ? "color" : "halves")
        let items = (try? MatchLevelRepository.items(type, level: level)) ?? []
        _game = State(initialValue: MatchGame(items: items))
    }

    private func submit(_ source: String, _ target: String) {
        guard !game.complete else { return }
        let success = game.match(source: source, target: target)
        wrongTarget = success ? nil : target
        selectedSource = nil
        selectedRight = nil
        if game.complete { onComplete(game.attempts, hintUsed) }
    }

    @ViewBuilder private func artwork(_ item: MatchItem, isTarget: Bool) -> some View {
        if mode == 3 {
            Circle().fill(color(item.id)).frame(width: 54, height: 54)
        } else if mode == 4 {
            YonaMatchView.halfArtwork(item)
                .frame(width: 56, height: 62)
                .offset(x: isTarget ? -14 : 14)
                .frame(width: 28, height: 62)
                .clipped()
        } else {
            Text(item.symbol).font(.system(size: 46))
        }
    }

    private func color(_ id: String) -> Color {
        switch id {
        case "red": return .red
        case "blue": return .blue
        case "green": return .green
        case "yellow": return .yellow
        case "purple": return .purple
        case "orange": return .orange
        case "pink": return .pink
        default: return .gray
        }
    }

    var body: some View {
        VStack(spacing: 14) {
            HStack {
                Text(mode == 2 ? "🔺" : (mode == 3 ? "🎨" : "🧩")).font(.system(size: 32))
                Spacer()
                Button {
                    selectedSource = game.items.first(where: { !game.matched.contains($0.id) })?.id
                    selectedRight = nil
                    if selectedSource != nil { hintUsed = true }
                } label: { Text("💡").font(.system(size: 34)) }
            }
            HStack(alignment: .top, spacing: 12) {
                ForEach([false, true], id: \.self) { isTarget in
                    let entries = isTarget ? game.targets : game.items
                    LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 8), count: entries.count <= 3 ? 1 : 2), spacing: 8) {
                        ForEach(entries) { item in
                            Button {
                                if isTarget {
                                    if let source = selectedSource { submit(source, item.id) }
                                    else { selectedRight = item.id }
                                } else {
                                    if let right = selectedRight { submit(item.id, right) }
                                    else { selectedSource = item.id }
                                }
                            } label: {
                                Group {
                                    if game.matched.contains(item.id) {
                                        Text("✓").font(.system(size: 42))
                                    } else {
                                        artwork(item, isTarget: isTarget)
                                    }
                                }
                                .frame(maxWidth: .infinity, minHeight: 110)
                                .background(game.matched.contains(item.id) ? Color.mint.opacity(0.5) :
                                    (wrongTarget == item.id && isTarget ? Color.red.opacity(0.2) :
                                    (selectedRight == item.id && isTarget ? Color.yellow.opacity(0.5) :
                                    (selectedSource == item.id && !isTarget ? Color.yellow.opacity(0.5) :
                                    (isTarget ? Color.purple.opacity(0.2) : Color.mint.opacity(0.2))))),
                                    in: RoundedRectangle(cornerRadius: 16))
                            }
                            .disabled(game.matched.contains(item.id))
                            .onDrag { NSItemProvider(object: item.id as NSString) }
                            .onDrop(of: [.plainText], isTargeted: nil) { providers in
                                guard isTarget, let provider = providers.first else { return false }
                                _ = provider.loadObject(ofClass: NSString.self) { value, _ in
                                    guard let source = value as? String else { return }
                                    DispatchQueue.main.async { submit(source, item.id) }
                                }
                                return true
                            }
                        }
                    }
                }
            }
            if wrongTarget != nil { Text("↻").font(.system(size: 28)).foregroundStyle(.red) }
            Text("⭐ \(game.matched.count)/\(game.items.count)").font(.system(size: 22))
        }
    }
}
