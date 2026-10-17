import SwiftUI

struct MemoryPlayScreen: View {
    let level: Int
    let onComplete: (Int, Bool) -> Void
    @State private var game: MemoryGame
    @State private var hintUsed = false
    @State private var hintPair: Set<Int> = []
    @State private var mismatchToken = UUID()

    init(level: Int, onComplete: @escaping (Int, Bool) -> Void) {
        self.level = level
        self.onComplete = onComplete
        let count = (try? LevelRepository.memoryLevels())?.first(where: { $0.order == level })?.pairs ?? 3
        let symbols = Array(["🦁", "🐘", "🐰", "🐼", "🐧", "🐱", "🐶", "🐻"].prefix(count))
        _game = State(initialValue: MemoryGame(pairs: symbols))
    }

    private func choose(_ index: Int) {
        guard !game.awaitingReset else { return }
        game.select(index)
        hintPair = []
        if game.isComplete {
            onComplete(game.attempts, hintUsed)
        } else if game.awaitingReset {
            let token = UUID()
            mismatchToken = token
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.85) {
                if mismatchToken == token { game.hideMismatch() }
            }
        }
    }

    var body: some View {
        VStack(spacing: 12) {
            HStack {
                Text("🦁").font(.system(size: 32))
                Spacer()
                Button {
                    if let first = game.cards.indices.first(where: { !game.matched.contains($0) }),
                       let second = game.cards.indices.first(where: { $0 != first && !game.matched.contains($0) && game.cards[$0] == game.cards[first] }) {
                        hintUsed = true
                        hintPair = [first, second]
                    }
                } label: { Text("💡").font(.system(size: 34)) }
            }
            let columns = game.cards.count <= 6 ? 3 : 4
            let rows = (game.cards.count + columns - 1) / columns
            GeometryReader { geometry in
                let cardHeight = max(76, (geometry.size.height - CGFloat(rows - 1) * 8) / CGFloat(rows))
            LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 8), count: columns), spacing: 8) {
                ForEach(game.cards.indices, id: \.self) { index in
                    Button { choose(index) } label: {
                        Text(game.revealed.contains(index) || game.matched.contains(index) || hintPair.contains(index) ? game.cards[index] : "❔")
                            .font(.system(size: rows <= 2 ? 60 : (rows == 3 ? 48 : 40)))
                            .frame(maxWidth: .infinity, minHeight: cardHeight)
                            .background(game.matched.contains(index) ? Color.mint.opacity(0.5) : Color.blue.opacity(0.25),
                                        in: RoundedRectangle(cornerRadius: 16))
                    }
                    .disabled(game.awaitingReset || game.matched.contains(index) || game.revealed.contains(index))
                }
            }
            }
        }
    }
}
