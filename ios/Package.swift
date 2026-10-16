// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "YonaMatchCore",
    platforms: [.iOS(.v16)],
    products: [.library(name: "YonaMatchCore", targets: ["YonaMatchCore"])],
    targets: [
        .target(name: "YonaMatchCore", resources: [.process("Resources")]),
        .testTarget(name: "YonaMatchCoreTests", dependencies: ["YonaMatchCore"])
    ]
)
