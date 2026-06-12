// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_shared",
  platforms: [
    .iOS("16.0")
  ],
  products: [
    .library(
      name: "_shared",
      type: .none,
      targets: ["_shared"]
    )
  ],
  dependencies: [
  ],
  targets: [
    .target(
      name: "_shared",
      dependencies: [
      ]
    )
  ]
)
