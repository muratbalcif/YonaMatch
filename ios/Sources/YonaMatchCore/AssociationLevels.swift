import Foundation

public struct AssociationPair: Equatable {
    public let source: MatchItem
    public let target: MatchItem
}
public struct AssociationLevel {
    public let order: Int
    public let pairs: [AssociationPair]
    public let idealAttempts: Int
}
private struct AssociationCatalogEntry: Decodable {
    let id: String
    let symbol: String
    let label: String
}
private struct AssociationPairEntry: Decodable {
    let source: String
    let target: String
}
private struct AssociationLevelEntry: Decodable {
    let order: Int
    let pairs: [AssociationPairEntry]
    let idealAttempts: Int
}
private struct AssociationContent: Decodable {
    let schemaVersion: Int
    let gameType: String
    let catalog: [AssociationCatalogEntry]
    let levels: [AssociationLevelEntry]
}
public enum AssociationLevelRepository {
    public static func levels() throws -> [AssociationLevel] {
        guard let url = Bundle.module.url(forResource: "association", withExtension: "json", subdirectory: "levels") else {
            throw LevelLoadError.missingResource
        }
        let content = try JSONDecoder().decode(AssociationContent.self, from: Data(contentsOf: url))
        guard content.schemaVersion == 1, content.gameType == "association",
              content.levels.count == 20, content.levels.map(\.order) == Array(1...20) else {
            throw LevelLoadError.invalidSchema
        }
        let catalog = Dictionary(uniqueKeysWithValues: content.catalog.map { ($0.id, MatchItem(id: $0.id, symbol: $0.symbol, label: $0.label)) })
        return try content.levels.map { entry in
            let pairs = try entry.pairs.map { pair -> AssociationPair in
                guard let source = catalog[pair.source], let target = catalog[pair.target] else {
                    throw LevelLoadError.invalidSchema
                }
                return AssociationPair(source: source, target: target)
            }
            guard Set(pairs.map { $0.source.id }).count == pairs.count,
                  Set(pairs.map { $0.target.id }).count == pairs.count else {
                throw LevelLoadError.invalidSchema
            }
            return AssociationLevel(order: entry.order, pairs: pairs, idealAttempts: entry.idealAttempts)
        }
    }
}
