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
                Text("Bölüm \(level)").font(.system(size: 20, weight: .semibold)).foregroundStyle(Color(red: 0.09, green: 0.27, blue: 0.47))
                Spacer()
                Button {
                    if let first = game.cards.indices.first(where: { !game.matched.contains($0) }),
                       let second = game.cards.indices.first(where: { $0 != first && !game.matched.contains($0) && game.cards[$0] == game.cards[first] }) {
                        hintUsed = true
                        hintPair = [first, second]
                    }
                } label: { Text("💡 İpucu").font(.system(size: 18, weight: .semibold)) }
            }
            let columns = 3
            let rows = (game.cards.count + columns - 1) / columns
            GeometryReader { geometry in
                let cardWidth = (geometry.size.width - 20) / CGFloat(columns)
                let cardHeight = min(cardWidth * 1.05, (geometry.size.height - CGFloat(rows - 1) * 10) / CGFloat(rows))
            LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 10), count: columns), spacing: 10) {
                ForEach(game.cards.indices, id: \.self) { index in
                    Button { choose(index) } label: {
                        Text(game.revealed.contains(index) || game.matched.contains(index) || hintPair.contains(index) ? game.cards[index] : "?")
                            .font(.system(size: game.revealed.contains(index) || game.matched.contains(index) || hintPair.contains(index) ? 44 : 50, weight: .semibold))
                            .frame(maxWidth: .infinity, minHeight: cardHeight)
                            .foregroundStyle(game.revealed.contains(index) || game.matched.contains(index) || hintPair.contains(index) ? Color(red: 0.13, green: 0.24, blue: 0.40) : .white.opacity(0.85))
                            .background(game.matched.contains(index) ? Color(red: 1, green: 0.9, blue: 0.65) : (game.revealed.contains(index) || hintPair.contains(index) ? Color.white : Color(red: 0.47, green: 0.75, blue: 0.98)),
                                        in: RoundedRectangle(cornerRadius: 15))
                            .shadow(color: .black.opacity(0.09), radius: 3, y: 2)
                    }
                    .disabled(game.awaitingReset || game.matched.contains(index) || game.revealed.contains(index))
                }
            }
            }
        }
    }
}
