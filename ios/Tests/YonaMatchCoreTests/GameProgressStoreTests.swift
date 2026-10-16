import XCTest
@testable import YonaMatchCore

final class GameProgressStoreTests: XCTestCase {
    func testStarsAndUnlockAreMonotonicForAllSixGames() {
        let suite = "yonamatch-progress-tests-\(UUID().uuidString)"
        let defaults = UserDefaults(suiteName: suite)!
        defer { defaults.removePersistentDomain(forName: suite) }
        let store = GameProgressStore(storage: defaults)
        for game in ["memory", "shadow", "shape", "color", "halves", "association"] {
            XCTAssertEqual(store.unlocked(game), 1)
            XCTAssertEqual(store.finish(game, level: 1, attempts: 7, idealAttempts: 3), 2)
            XCTAssertEqual(store.bestStars(game, level: 1), 2)
            XCTAssertEqual(store.unlocked(game), 2)
            XCTAssertEqual(store.finish(game, level: 1, attempts: 3, idealAttempts: 3), 3)
            XCTAssertEqual(store.bestStars(game, level: 1), 3)
            XCTAssertEqual(store.finish(game, level: 1, attempts: 10, idealAttempts: 3, hintsUsed: true), 1)
            XCTAssertEqual(store.bestStars(game, level: 1), 3)
            XCTAssertEqual(store.finish(game, level: 20, attempts: 3, idealAttempts: 3), 3)
            XCTAssertEqual(store.unlocked(game), 20)
        }
    }

    func testMigrationPreservesLegacyUnlocksAfterRelaunch() {
        let suite = "yonamatch-migration-\(UUID().uuidString)"
        let defaults = UserDefaults(suiteName: suite)!
        defer { defaults.removePersistentDomain(forName: suite) }
        defaults.set(8, forKey: "shadow_unlocked")
        defaults.set(6, forKey: "association_unlocked")
        var store = GameProgressStore(storage: defaults)
        XCTAssertEqual(store.unlocked("shadow"), 8)
        XCTAssertEqual(store.unlocked("association"), 6)
        XCTAssertEqual(store.finish("shadow", level: 8, attempts: 3, idealAttempts: 3), 3)
        store = GameProgressStore(storage: defaults)
        XCTAssertEqual(store.unlocked("shadow"), 9)
        XCTAssertEqual(store.bestStars("shadow", level: 8), 3)
        XCTAssertEqual(store.unlocked("association"), 6)
    }
}
