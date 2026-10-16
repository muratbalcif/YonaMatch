import XCTest
@testable import YonaMatchCore

final class MatchGameTests: XCTestCase {
    func testCorrectMatchCompletes() {
        var game = MatchGame(items: [.init(id: "circle", symbol: "●", label: "Daire")])
        XCTAssertTrue(game.match(source: "circle", target: "circle"))
        XCTAssertTrue(game.complete)
        XCTAssertEqual(game.attempts, 1)
    }
    func testIncorrectMatchDoesNotComplete() {
        var game = MatchGame(items: [
            .init(id: "circle", symbol: "●", label: "Daire"),
            .init(id: "square", symbol: "■", label: "Kare")
        ])
        XCTAssertFalse(game.match(source: "circle", target: "square"))
        XCTAssertEqual(game.matched.count, 0)
        XCTAssertEqual(game.attempts, 1)
    }
}
