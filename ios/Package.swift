// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "YonaMatchCore",
    platforms: [.iOS(.v16)],
    products: [.library(name: "YonaMatchCore", targets: ["YonaMatchCore"])],
    targets: [.target(name: "YonaMatchCore"), .testTarget(name: "YonaMatchCoreTests", dependencies: ["YonaMatchCore"])]
)
