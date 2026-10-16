import XCTest
@testable import YonaMatchCore

final class ShadowLevelTests: XCTestCase {
    func testTwentyLevelsLoad() throws {
        let levels = try ShadowLevelRepository.levels()
        XCTAssertEqual(levels.count, 20)
        XCTAssertEqual(levels.first?.order, 1)
        XCTAssertEqual(levels.last?.order, 20)
    }
    func testAllLevelItemsResolve() throws {
        for level in 1...20 {
            let items = try ShadowLevelRepository.items(for: level)
            XCTAssertEqual(Set(items.map(\.id)).count, items.count)
            XCTAssertGreaterThanOrEqual(items.count, 3)
        }
    }
}
