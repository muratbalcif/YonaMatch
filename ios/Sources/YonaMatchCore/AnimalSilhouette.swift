import SwiftUI

/// Native vector silhouette, scaled to fit a 128×128 design grid.
public struct AnimalSilhouette: View {
    public let id: String
    public init(id: String) { self.id = id }
    public var body: some View {
        SilhouettePath(id: id)
            .fill(Color(red: 0.145, green: 0.188, blue: 0.278))
            .accessibilityLabel("Gölge")
    }
}
private struct SilhouettePath: Shape {
    let id: String
    func path(in rect: CGRect) -> Path {
        let coordinates: [String: [CGPoint]] = [
            "lion": [CGPoint(x: 64, y: 8), CGPoint(x: 80, y: 17), CGPoint(x: 99, y: 22), CGPoint(x: 112, y: 42), CGPoint(x: 119, y: 63), CGPoint(x: 109, y: 86), CGPoint(x: 95, y: 107), CGPoint(x: 64, y: 122), CGPoint(x: 33, y: 107), CGPoint(x: 19, y: 86), CGPoint(x: 9, y: 63), CGPoint(x: 16, y: 42), CGPoint(x: 29, y: 22), CGPoint(x: 48, y: 17)],
            "elephant": [CGPoint(x: 16, y: 54), CGPoint(x: 37, y: 21), CGPoint(x: 60, y: 38), CGPoint(x: 102, y: 38), CGPoint(x: 109, y: 80), CGPoint(x: 109, y: 104), CGPoint(x: 92, y: 104), CGPoint(x: 92, y: 85), CGPoint(x: 76, y: 85), CGPoint(x: 76, y: 105), CGPoint(x: 57, y: 105), CGPoint(x: 57, y: 86), CGPoint(x: 42, y: 86), CGPoint(x: 42, y: 105), CGPoint(x: 23, y: 105), CGPoint(x: 23, y: 81)],
            "rabbit": [CGPoint(x: 40, y: 62), CGPoint(x: 35, y: 7), CGPoint(x: 57, y: 52), CGPoint(x: 79, y: 8), CGPoint(x: 82, y: 60), CGPoint(x: 106, y: 96), CGPoint(x: 64, y: 121), CGPoint(x: 22, y: 96)],
            "cat": [CGPoint(x: 20, y: 42), CGPoint(x: 21, y: 14), CGPoint(x: 46, y: 31), CGPoint(x: 82, y: 31), CGPoint(x: 107, y: 14), CGPoint(x: 108, y: 42), CGPoint(x: 111, y: 87), CGPoint(x: 64, y: 116), CGPoint(x: 17, y: 87)],
            "dog": [CGPoint(x: 29, y: 30), CGPoint(x: 99, y: 30), CGPoint(x: 118, y: 21), CGPoint(x: 116, y: 66), CGPoint(x: 101, y: 76), CGPoint(x: 105, y: 111), CGPoint(x: 64, y: 118), CGPoint(x: 23, y: 111), CGPoint(x: 27, y: 76), CGPoint(x: 12, y: 66), CGPoint(x: 10, y: 21)],
            "bear": [CGPoint(x: 24, y: 38), CGPoint(x: 26, y: 13), CGPoint(x: 49, y: 28), CGPoint(x: 79, y: 28), CGPoint(x: 102, y: 13), CGPoint(x: 104, y: 38), CGPoint(x: 111, y: 84), CGPoint(x: 64, y: 118), CGPoint(x: 17, y: 84)],
            "penguin": [CGPoint(x: 64, y: 7), CGPoint(x: 102, y: 55), CGPoint(x: 111, y: 85), CGPoint(x: 94, y: 91), CGPoint(x: 97, y: 115), CGPoint(x: 76, y: 115), CGPoint(x: 64, y: 106), CGPoint(x: 52, y: 115), CGPoint(x: 31, y: 115), CGPoint(x: 34, y: 91), CGPoint(x: 17, y: 85), CGPoint(x: 26, y: 55)],
            "panda": [CGPoint(x: 27, y: 42), CGPoint(x: 25, y: 13), CGPoint(x: 51, y: 26), CGPoint(x: 77, y: 26), CGPoint(x: 103, y: 13), CGPoint(x: 101, y: 42), CGPoint(x: 108, y: 88), CGPoint(x: 64, y: 118), CGPoint(x: 20, y: 88)],
        ]
        var result = Path()
        guard let points = coordinates[id], let first = points.first else { return result }
        let transform = CGAffineTransform(scaleX: rect.width / 128, y: rect.height / 128)
        result.move(to: first.applying(transform))
        for point in points.dropFirst() { result.addLine(to: point.applying(transform)) }
        result.closeSubpath()
        return result
    }
}
