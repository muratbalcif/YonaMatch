import Foundation

public struct ShadowLevel: Decodable {
    public let id: String
    public let order: Int
    public let items: [String]
    public let idealAttempts: Int
}

private struct ShadowCatalogItem: Decodable {
    let id: String
    let emoji: String
    let label: String
}
private struct ShadowLevelFile: Decodable {
    let schemaVersion: Int
    let gameType: String
    let catalog: [ShadowCatalogItem]
    let levels: [ShadowLevel]
}

public enum ShadowLevelRepository {
    private static func load() throws -> ShadowLevelFile {
        guard let url = Bundle.module.url(forResource: "shadow", withExtension: "json", subdirectory: "levels") else {
            throw LevelLoadError.missingResource
        }
        let file = try JSONDecoder().decode(ShadowLevelFile.self, from: Data(contentsOf: url))
        guard file.schemaVersion == 1, file.gameType == "shadow",
              file.levels.count == 20,
              file.levels.map(\.order) == Array(1...20) else { throw LevelLoadError.invalidSchema }
        return file
    }
    public static func levels() throws -> [ShadowLevel] { try load().levels }
    public static func items(for level: Int) throws -> [ShadowItem] {
        let file = try load()
        guard let entry = file.levels.first(where: { $0.order == level }) else { throw LevelLoadError.invalidSchema }
        let catalog = Dictionary(uniqueKeysWithValues: file.catalog.map { ($0.id, $0) })
        return try entry.items.map { id in
            guard let item = catalog[id] else { throw LevelLoadError.invalidSchema }
            return ShadowItem(id: item.id, emoji: item.emoji, label: item.label)
        }
    }
}
