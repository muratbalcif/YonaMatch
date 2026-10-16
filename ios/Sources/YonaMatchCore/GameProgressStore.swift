import Foundation

public struct GameProgressStore {
    private let storage: UserDefaults
    public init(storage: UserDefaults = .standard) {
        self.storage = storage
        if !storage.bool(forKey: "ym_migration_v1_done") {
            for game in ["memory", "shadow", "shape", "color", "halves", "association"] {
                let old = storage.integer(forKey: "\(game)_unlocked")
                let key = "ym_progress_\(game)"
                storage.set(max(1, min(20, max(old, storage.integer(forKey: key)))), forKey: key)
            }
            storage.set(true, forKey: "ym_migration_v1_done")
        }
    }

    public func unlocked(_ game: String) -> Int {
        min(20, max(1, storage.integer(forKey: "ym_progress_\(game)")))
    }

    public func bestStars(_ game: String, level: Int) -> Int {
        min(3, max(0, storage.integer(forKey: "ym_stars_\(game)_\(level)")))
    }

    @discardableResult
    public func finish(_ game: String, level: Int, attempts: Int, idealAttempts: Int, hintsUsed: Bool = false) -> Int {
        let stars = hintsUsed ? 1 : (attempts <= idealAttempts ? 3 : 2)
        storage.set(max(bestStars(game, level: level), stars), forKey: "ym_stars_\(game)_\(level)")
        storage.set(max(unlocked(game), min(20, level + 1)), forKey: "ym_progress_\(game)")
        return stars
    }
}
