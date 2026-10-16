import Foundation

public struct MatchItem: Identifiable, Equatable {
    public let id: String
    public let symbol: String
    public let label: String
    public init(id: String, symbol: String, label: String) {
        self.id = id; self.symbol = symbol; self.label = label
    }
}
public struct MatchGame {
    public let items: [MatchItem]
    public let targets: [MatchItem]
    public private(set) var matched: Set<String> = []
    public private(set) var attempts = 0
    public init(items: [MatchItem]) {
        precondition(!items.isEmpty && Set(items.map(\.id)).count == items.count)
        self.items = items
        self.targets = items.shuffled()
    }
    @discardableResult public mutating func match(source: String, target: String) -> Bool {
        guard items.contains(where: { $0.id == source }),
              items.contains(where: { $0.id == target }),
              !matched.contains(source), !matched.contains(target) else { return false }
        attempts += 1
        if source == target { matched.insert(source); return true }
        return false
    }
    public var complete: Bool { matched.count == items.count }
}
