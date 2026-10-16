import SwiftUI

/// Lightweight native silhouettes for the eight halves-puzzle objects.
/// Coordinates are normalized to a 100 x 100 artboard.
@available(iOS 16.0, *)
struct HalfArtworkShape: Shape {
    let id: String

    func path(in rect: CGRect) -> Path {
        var p = Path()
        switch id {
        case "apple":
            p.move(to: CGPoint(x: 50, y: 31))
            p.addCurve(to: CGPoint(x: 19, y: 67), control1: CGPoint(x: 30, y: 12), control2: CGPoint(x: 11, y: 36))
            p.addCurve(to: CGPoint(x: 50, y: 89), control1: CGPoint(x: 25, y: 91), control2: CGPoint(x: 42, y: 96))
            p.addCurve(to: CGPoint(x: 81, y: 67), control1: CGPoint(x: 58, y: 96), control2: CGPoint(x: 75, y: 91))
            p.addCurve(to: CGPoint(x: 50, y: 31), control1: CGPoint(x: 89, y: 36), control2: CGPoint(x: 70, y: 12))
        case "butterfly":
            p.move(to: CGPoint(x: 48, y: 49))
            p.addCurve(to: CGPoint(x: 36, y: 58), control1: CGPoint(x: 9, y: 1), control2: CGPoint(x: 1, y: 48))
            p.addCurve(to: CGPoint(x: 48, y: 63), control1: CGPoint(x: 2, y: 58), control2: CGPoint(x: 15, y: 99))
            p.closeSubpath()
            p.move(to: CGPoint(x: 52, y: 49))
            p.addCurve(to: CGPoint(x: 64, y: 58), control1: CGPoint(x: 91, y: 1), control2: CGPoint(x: 99, y: 48))
            p.addCurve(to: CGPoint(x: 52, y: 63), control1: CGPoint(x: 98, y: 58), control2: CGPoint(x: 85, y: 99))
            p.closeSubpath()
        case "fish":
            p.addEllipse(in: CGRect(x: 9, y: 25, width: 68, height: 52))
            p.move(to: CGPoint(x: 70, y: 49))
            p.addLine(to: CGPoint(x: 93, y: 27))
            p.addLine(to: CGPoint(x: 93, y: 75))
            p.closeSubpath()
        case "car":
            p.move(to: CGPoint(x: 10, y: 47))
            p.addLine(to: CGPoint(x: 17, y: 47))
            p.addLine(to: CGPoint(x: 27, y: 32))
            p.addQuadCurve(to: CGPoint(x: 38, y: 27), control: CGPoint(x: 30, y: 27))
            p.addLine(to: CGPoint(x: 64, y: 27))
            p.addQuadCurve(to: CGPoint(x: 75, y: 34), control: CGPoint(x: 71, y: 27))
            p.addLine(to: CGPoint(x: 84, y: 47))
            p.addLine(to: CGPoint(x: 90, y: 47))
            p.addLine(to: CGPoint(x: 90, y: 75))
            p.addLine(to: CGPoint(x: 10, y: 75))
            p.closeSubpath()
        case "house":
            p.move(to: CGPoint(x: 14, y: 46))
            p.addLine(to: CGPoint(x: 50, y: 15))
            p.addLine(to: CGPoint(x: 86, y: 46))
            p.addLine(to: CGPoint(x: 77, y: 46))
            p.addLine(to: CGPoint(x: 77, y: 88))
            p.addLine(to: CGPoint(x: 23, y: 88))
            p.addLine(to: CGPoint(x: 23, y: 46))
            p.closeSubpath()
        case "flower":
            for i in 0..<8 {
                let angle = CGFloat(i) * .pi / 4
                let cx = 50 + 23 * cos(angle)
                let cy = 50 + 23 * sin(angle)
                p.addEllipse(in: CGRect(x: cx - 15, y: cy - 15, width: 30, height: 30))
            }
            p.addEllipse(in: CGRect(x: 38, y: 38, width: 24, height: 24))
        case "rocket":
            p.move(to: CGPoint(x: 50, y: 10))
            p.addCurve(to: CGPoint(x: 39, y: 76), control1: CGPoint(x: 29, y: 27), control2: CGPoint(x: 32, y: 57))
            p.addLine(to: CGPoint(x: 22, y: 81))
            p.addLine(to: CGPoint(x: 22, y: 59))
            p.addLine(to: CGPoint(x: 36, y: 53))
            p.addLine(to: CGPoint(x: 44, y: 78))
            p.addLine(to: CGPoint(x: 50, y: 96))
            p.addLine(to: CGPoint(x: 56, y: 78))
            p.addLine(to: CGPoint(x: 64, y: 53))
            p.addLine(to: CGPoint(x: 78, y: 59))
            p.addLine(to: CGPoint(x: 78, y: 81))
            p.addLine(to: CGPoint(x: 61, y: 76))
            p.addCurve(to: CGPoint(x: 50, y: 10), control1: CGPoint(x: 68, y: 57), control2: CGPoint(x: 71, y: 27))
        case "turtle":
            p.addEllipse(in: CGRect(x: 17, y: 26, width: 63, height: 52))
            p.addEllipse(in: CGRect(x: 74, y: 36, width: 24, height: 28))
            p.addEllipse(in: CGRect(x: 22, y: 73, width: 18, height: 12))
            p.addEllipse(in: CGRect(x: 57, y: 73, width: 18, height: 12))
        default:
            p.addRoundedRect(in: CGRect(x: 18, y: 18, width: 64, height: 64), cornerSize: CGSize(width: 12, height: 12))
        }
        return p.applying(CGAffineTransform(translationX: rect.minX, y: rect.minY)
            .concatenating(CGAffineTransform(scaleX: rect.width / 100, y: rect.height / 100)))
    }
}
