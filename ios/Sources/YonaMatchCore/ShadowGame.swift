import Foundation

public struct ShadowItem: Identifiable, Equatable {
    public let id: String
    public let emoji: String
    public let label: String
    public init(id: String, emoji: String, label: String) {
        self.id = id; self.emoji = emoji; self.label = label
    }
}

public struct ShadowGame {
    public let items: [ShadowItem]
    public let targets: [ShadowItem]
    public private(set) var matched: Set<String> = []
    public private(set) var attempts = 0
    public init(items: [ShadowItem]) {
        precondition(Set(items.map(\.id)).count == items.count)
        self.items = items
        self.targets = items.shuffled()
    }
    @discardableResult public mutating func match(sourceId: String, targetId: String) -> Bool {
        guard items.contains(where: { $0.id == sourceId }),
              targets.contains(where: { $0.id == targetId }),
              !matched.contains(sourceId), !matched.contains(targetId) else { return false }
        attempts += 1
        if sourceId == targetId { matched.insert(sourceId); return true }
        return false
    }
    public var isComplete: Bool { matched.count == items.count }
}
