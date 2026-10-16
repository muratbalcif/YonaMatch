import XCTest
@testable import YonaMatchCore

final class AssociationLevelTests: XCTestCase {
    func testTwentyLevelsAndUniquePairs() throws {
        let levels = try AssociationLevelRepository.levels()
        XCTAssertEqual(levels.count, 20)
        XCTAssertEqual(levels.map(\.order), Array(1...20))
        for level in levels {
            XCTAssertGreaterThanOrEqual(level.idealAttempts, level.pairs.count)
            XCTAssertEqual(Set(level.pairs.map { $0.source.id }).count, level.pairs.count)
            XCTAssertEqual(Set(level.pairs.map { $0.target.id }).count, level.pairs.count)
        }
    }

    func testAssociationsMatchBySourceIdentity() throws {
        let level = try XCTUnwrap(AssociationLevelRepository.levels().first)
        var game = MatchGame(items: level.pairs.map { $0.source })
        let first = try XCTUnwrap(level.pairs.first)
        XCTAssertTrue(game.match(source: first.source.id, target: first.source.id))
        XCTAssertFalse(game.match(source: first.source.id, target: first.source.id))
    }
}
