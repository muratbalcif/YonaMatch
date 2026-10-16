import Foundation

public struct MemoryLevel: Decodable, Equatable {
    public let id: String
    public let order: Int
    public let pairs: Int
    public let theme: String
    public let idealAttempts: Int
}

private struct LevelFile: Decodable {
    let schemaVersion: Int
    let gameType: String
    let levels: [MemoryLevel]
}

public enum LevelLoadError: Error { case missingResource, invalidSchema }

public enum LevelRepository {
    public static func memoryLevels() throws -> [MemoryLevel] {
        guard let url = Bundle.module.url(forResource: "memory", withExtension: "json", subdirectory: "levels") else {
            throw LevelLoadError.missingResource
        }
        let file = try JSONDecoder().decode(LevelFile.self, from: Data(contentsOf: url))
        guard file.schemaVersion == 1, file.gameType == "memory",
              file.levels.count == 20,
              file.levels.map(\.order) == Array(1...20) else { throw LevelLoadError.invalidSchema }
        return file.levels
    }
}

public struct GameProgress {
    public let unlocked: Int
    public let bestStars: [Int: Int]
}

public final class ProgressRepository {
    private let store: UserDefaults
    public init(store: UserDefaults = .standard) { self.store = store }
    public var unlocked: Int { max(1, min(20, store.integer(forKey: "memory_unlocked"))) }
    public func bestStars(for level: Int) -> Int { max(0, min(3, store.integer(forKey: "memory_stars_\(level)"))) }
    public func finish(level: Int, stars: Int) {
        precondition((1...20).contains(level) && (1...3).contains(stars))
        store.set(max(unlocked, min(20, level + 1)), forKey: "memory_unlocked")
        store.set(max(bestStars(for: level), stars), forKey: "memory_stars_\(level)")
    }
}
