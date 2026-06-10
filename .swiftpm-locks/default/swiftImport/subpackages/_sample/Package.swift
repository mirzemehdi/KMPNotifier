// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_sample",
  platforms: [
    .iOS("16.0")
  ],
  products: [
    .library(
      name: "_sample",
      type: .none,
      targets: ["_sample"]
    )
  ],
  dependencies: [
  ],
  targets: [
    .target(
      name: "_sample",
      dependencies: [
      ]
    )
  ]
)
