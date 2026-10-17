import SwiftUI

struct ShadowPlayScreen: View {
    let level: Int
    let onComplete: (Int, Bool) -> Void
    @State private var game: ShadowGame
    @State private var selectedSource: String?
    @State private var selectedRight: String?
    @State private var hintUsed = false
    @State private var wrongTarget: String?

    init(level: Int, onComplete: @escaping (Int, Bool) -> Void) {
        self.level = level
        self.onComplete = onComplete
        _game = State(initialValue: ShadowGame(items: (try? ShadowLevelRepository.items(for: level)) ?? []))
    }

    private func submit(_ source: String, _ target: String) {
        guard !game.isComplete else { return }
        let correct = game.match(sourceId: source, targetId: target)
        wrongTarget = correct ? nil : target
        selectedSource = nil
        selectedRight = nil
        if game.isComplete { onComplete(game.attempts, hintUsed) }
    }

    var body: some View {
        VStack(spacing: 12) {
            HStack {
                Text("🐘").font(.system(size: 32))
                Spacer()
                Button {
                    selectedSource = game.items.first(where: { !game.matched.contains($0.id) })?.id
                    selectedRight = nil
                    if selectedSource != nil { hintUsed = true }
                } label: { Text("💡").font(.system(size: 34)) }
            }
            GeometryReader { geometry in
                    HStack(alignment: .top, spacing: 10) {
                let cardHeight = max(76, (geometry.size.height - CGFloat((isShadow ? game.items.count : (game.items.count <= 3 ? game.items.count : (game.items.count + 1) / 2)) - 1) * 8) / CGFloat(isShadow ? game.items.count : (game.items.count <= 3 ? game.items.count : (game.items.count + 1) / 2)))
                ForEach([false, true], id: \.self) { target in
                    let entries = target ? game.targets : game.items
                    LazyVGrid(columns: [GridItem(.flexible(), spacing: 8)], spacing: 8) {
                        ForEach(entries) { item in
                            Button {
                                if target {
                                    if let source = selectedSource { submit(source, item.id) }
                                    else { selectedRight = item.id }
                                    else { selectedRight = item.id }
                                } else {
                                    if let right = selectedRight { submit(item.id, right) }
                                    else { selectedSource = item.id }
                                }
                            } label: {
                                Text(game.matched.contains(item.id) ? "✓" : item.emoji)
                                    .font(.system(size: 48))
                                    .saturation(target ? 0 : 1)
                                    .opacity(target && !game.matched.contains(item.id) ? 0.45 : 1)
                                    .frame(maxWidth: .infinity, minHeight: cardHeight)
                                    .background(game.matched.contains(item.id) ? Color.mint.opacity(0.5) :
                                        (selectedSource == item.id && !target ? Color.yellow.opacity(0.5) :
                                        (wrongTarget == item.id && target ? Color.red.opacity(0.2) :
                                        (selectedRight == item.id && target ? Color.yellow.opacity(0.5) :
                                        (target ? Color.purple.opacity(0.2) : Color.mint.opacity(0.2))))),
                                        in: RoundedRectangle(cornerRadius: 16))
                            }
                            .disabled(game.matched.contains(item.id))
                            .onDrag { NSItemProvider(object: item.id as NSString) }
                            .onDrop(of: [.plainText], isTargeted: nil) { providers in
                                guard target, let provider = providers.first else { return false }
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
            }
            Text("⭐ \(game.matched.count)/\(game.items.count)").font(.system(size: 22))
        }
    }
}
