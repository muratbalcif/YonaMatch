import XCTest
@testable import YonaMatchCore

final class MemoryGameTests: XCTestCase {
    func testMatchingAllPairsCompletesGame() {
        var game = MemoryGame(pairs: ["🐱"])
        game.select(0)
        game.select(1)
        XCTAssertTrue(game.isComplete)
        XCTAssertEqual(game.attempts, 1)
    }
    func testCannotSelectSameCardTwice() {
        var game = MemoryGame(pairs: ["🐱"])
        game.select(0)
        game.select(0)
        XCTAssertEqual(game.attempts, 0)
    }
}
