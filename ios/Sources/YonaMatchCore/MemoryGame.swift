import Foundation

public struct MemoryGame {
    public private(set) var cards: [String]
    public private(set) var revealed: Set<Int> = []
    public private(set) var matched: Set<Int> = []
    public private(set) var attempts = 0
    public private(set) var awaitingReset = false

    public init(pairs: [String] = ["🐱", "🐶", "🐰"]) {
        self.cards = (pairs + pairs).shuffled()
    }

    public var isComplete: Bool { matched.count == cards.count }
    public mutating func select(_ index: Int) {
        guard cards.indices.contains(index), !awaitingReset,
              !matched.contains(index), !revealed.contains(index) else { return }
        revealed.insert(index)
        if revealed.count == 2 {
            attempts += 1
            let pair = Array(revealed)
            if cards[pair[0]] == cards[pair[1]] {
                matched.formUnion(revealed)
                revealed.removeAll()
            } else { awaitingReset = true }
        }
    }
    public mutating func hideMismatch() {
        guard awaitingReset else { return }
        revealed.removeAll()
        awaitingReset = false
    }
}
