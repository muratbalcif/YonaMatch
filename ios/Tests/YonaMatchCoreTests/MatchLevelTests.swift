import XCTest
@testable import YonaMatchCore

final class MatchLevelTests: XCTestCase {
    func testTwentyLevelsPerGame() throws {
        for type in ["shape", "color"] {
            let levels = try MatchLevelRepository.levels(type)
            XCTAssertEqual(levels.count, 20)
            for level in 1...20 {
                let items = try MatchLevelRepository.items(type, level: level)
                XCTAssertGreaterThanOrEqual(items.count, 3)
                XCTAssertEqual(items.count, Set(items.map(\.id)).count)
            }
        }
    }
}
