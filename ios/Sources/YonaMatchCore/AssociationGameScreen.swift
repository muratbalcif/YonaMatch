import SwiftUI
import UniformTypeIdentifiers

/// Independent association screen: owns selection, hints and match progress.
struct AssociationPlayScreen: View {
    let level: Int
    let onComplete: (Int, Bool) -> Void
    @State private var game: MatchGame
    @State private var selectedSource: String?
    @State private var hintUsed = false
    @State private var wrongTarget: String?
    private let associations: [String: MatchItem]

    init(level: Int, onComplete: @escaping (Int, Bool) -> Void) {
        self.level = level
        self.onComplete = onComplete
        let pairs = ((try? AssociationLevelRepository.levels()) ?? [])
            .first(where: { $0.order == level })?.pairs ?? []
        let mapping = Dictionary(uniqueKeysWithValues: pairs.map { ($0.source.id, $0.target) })
        self.associations = mapping
        _game = State(initialValue: MatchGame(items: pairs.map { $0.source }, associationTargets: mapping))
    }

    private func match(_ source: String, _ target: String) {
        guard !game.complete else { return }
        let success = game.match(source: source, target: target)
        wrongTarget = success ? nil : target
        selectedSource = nil
        if game.complete { onComplete(game.attempts, hintUsed) }
    }

    var body: some View {
        VStack(spacing: 14) {
            HStack {
                Text("🔗").font(.system(size: 32))
                Spacer()
                Button {
                    selectedSource = game.items.first(where: { !game.matched.contains($0.id) })?.id
                    if selectedSource != nil { hintUsed = true }
                } label: { Text("💡").font(.system(size: 34)) }
            }
            HStack(alignment: .top, spacing: 12) {
                LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 8), count: game.items.count <= 3 ? 1 : 2), spacing: 8) {
                    ForEach(game.items) { item in
                        Button {
                            if !game.matched.contains(item.id) { selectedSource = item.id }
                        } label: {
                            Text(game.matched.contains(item.id) ? "✓" : item.symbol)
                                .font(.system(size: 46))
                                .frame(maxWidth: .infinity, minHeight: 110)
                                .background(game.matched.contains(item.id) ? Color.mint.opacity(0.5) : (selectedSource == item.id ? Color.yellow.opacity(0.5) : Color.mint.opacity(0.2)), in: RoundedRectangle(cornerRadius: 16))
                        }
                        .disabled(game.matched.contains(item.id))
                        .onDrag { NSItemProvider(object: item.id as NSString) }
                    }
                }
                LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 8), count: game.targets.count <= 3 ? 1 : 2), spacing: 8) {
                    ForEach(game.targets) { item in
                        let sourceId = associations.first(where: { $0.value.id == item.id })?.key ?? item.id
                        Button {
                            guard let source = selectedSource else { return }
                            match(source, item.id)
                        } label: {
                            Text(game.matched.contains(sourceId) ? "✓" : item.symbol)
                                .font(.system(size: 46))
                                .frame(maxWidth: .infinity, minHeight: 110)
                                .background(game.matched.contains(sourceId) ? Color.mint.opacity(0.5) : (wrongTarget == item.id ? Color.red.opacity(0.2) : Color.purple.opacity(0.2)), in: RoundedRectangle(cornerRadius: 16))
                        }
                        .disabled(game.matched.contains(sourceId))
                        .onDrop(of: [.plainText], isTargeted: nil) { providers in
                            guard let provider = providers.first else { return false }
                            _ = provider.loadObject(ofClass: NSString.self) { value, _ in
                                guard let source = value as? String else { return }
                                DispatchQueue.main.async { match(source, item.id) }
                            }
                            return true
                        }
                    }
                }
            }
            if wrongTarget != nil { Text("↻").font(.system(size: 28)).foregroundStyle(.red) }
            Text("⭐ \(game.matched.count)/\(game.items.count)").font(.system(size: 22))
        }
    }
}
