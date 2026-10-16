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

    func testAssociationsUseRealTargetIds() throws {
        let level = try XCTUnwrap(AssociationLevelRepository.levels().first)
        let targets = Dictionary(uniqueKeysWithValues: level.pairs.map { ($0.source.id, $0.target) })
        var game = MatchGame(items: level.pairs.map { $0.source }, associationTargets: targets)
        let first = try XCTUnwrap(level.pairs.first)
        let wrong = try XCTUnwrap(level.pairs.first(where: { $0.target.id != first.target.id }))
        XCTAssertFalse(game.match(source: first.source.id, target: wrong.target.id))
        XCTAssertTrue(game.match(source: first.source.id, target: first.target.id))
        XCTAssertFalse(game.match(source: first.source.id, target: first.target.id))
        for pair in level.pairs.dropFirst() {
            XCTAssertTrue(game.match(source: pair.source.id, target: pair.target.id))
        }
        XCTAssertTrue(game.complete)
    }
}
