import Foundation

public struct MatchLevel: Decodable {
    public let id: String
    public let order: Int
    public let items: [String]
    public let idealAttempts: Int
}
private struct MatchCatalogItem: Decodable {
    let id: String
    let symbol: String
    let label: String
}
private struct MatchContent: Decodable {
    let schemaVersion: Int
    let gameType: String
    let catalog: [MatchCatalogItem]
    let levels: [MatchLevel]
}
public enum MatchLevelRepository {
    private static func load(_ type: String) throws -> MatchContent {
        guard type == "shape" || type == "color" || type == "halves" else { throw LevelLoadError.invalidSchema }
        guard let url = Bundle.module.url(forResource: type, withExtension: "json", subdirectory: "levels") else {
            throw LevelLoadError.missingResource
        }
        let data = try JSONDecoder().decode(MatchContent.self, from: Data(contentsOf: url))
        guard data.schemaVersion == 1, data.gameType == type, data.levels.count == 20,
              data.levels.map(\.order) == Array(1...20) else { throw LevelLoadError.invalidSchema }
        return data
    }
    public static func levels(_ type: String) throws -> [MatchLevel] { try load(type).levels }
    public static func items(_ type: String, level: Int) throws -> [MatchItem] {
        let data = try load(type)
        guard let selected = data.levels.first(where: { $0.order == level }) else { throw LevelLoadError.invalidSchema }
        let catalog = Dictionary(uniqueKeysWithValues: data.catalog.map { ($0.id, $0) })
        return try selected.items.map { id in
            guard let item = catalog[id] else { throw LevelLoadError.invalidSchema }
            return MatchItem(id: id, symbol: item.symbol, label: item.label)
        }
    }
}
