import XCTest
@testable import YonaMatchCore

final class ShadowGameTests: XCTestCase {
    func testCorrectMatch() {
        var game = ShadowGame(items: [.init(id: "lion", emoji: "🦁", label: "Aslan")])
        XCTAssertTrue(game.match(sourceId: "lion", targetId: "lion"))
        XCTAssertTrue(game.isComplete)
        XCTAssertEqual(game.attempts, 1)
        XCTAssertFalse(game.match(sourceId: "lion", targetId: "lion"))
    }
    func testWrongMatch() {
        var game = ShadowGame(items: [.init(id: "lion", emoji: "🦁", label: "Aslan"), .init(id: "cat", emoji: "🐱", label: "Kedi")])
        XCTAssertFalse(game.match(sourceId: "lion", targetId: "cat"))
        XCTAssertEqual(game.attempts, 1)
        XCTAssertFalse(game.isComplete)
    }
}
