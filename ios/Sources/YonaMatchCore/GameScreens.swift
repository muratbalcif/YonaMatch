import SwiftUI

struct MemoryGameScreen<Content: View>: View {
    let content: Content
    init(@ViewBuilder content: () -> Content) { self.content = content() }
    var body: some View { content }
}
struct ShadowGameScreen<Content: View>: View {
    let content: Content
    init(@ViewBuilder content: () -> Content) { self.content = content() }
    var body: some View { content }
}
struct ShapeGameScreen<Content: View>: View {
    let content: Content
    init(@ViewBuilder content: () -> Content) { self.content = content() }
    var body: some View { content }
}
struct ColorGameScreen<Content: View>: View {
    let content: Content
    init(@ViewBuilder content: () -> Content) { self.content = content() }
    var body: some View { content }
}
struct HalvesGameScreen<Content: View>: View {
    let content: Content
    init(@ViewBuilder content: () -> Content) { self.content = content() }
    var body: some View { content }
}
struct AssociationGameScreen<Content: View>: View {
    let content: Content
    init(@ViewBuilder content: () -> Content) { self.content = content() }
    var body: some View { content }
}

struct GameScreenHost<Content: View>: View {
    let mode: Int
    let content: Content
    init(mode: Int, @ViewBuilder content: () -> Content) {
        self.mode = mode
        self.content = content()
    }
    @ViewBuilder var body: some View {
        switch mode {
        case 0: MemoryGameScreen { content }
        case 1: ShadowGameScreen { content }
        case 2: ShapeGameScreen { content }
        case 3: ColorGameScreen { content }
        case 4: HalvesGameScreen { content }
        default: AssociationGameScreen { content }
        }
    }
}
