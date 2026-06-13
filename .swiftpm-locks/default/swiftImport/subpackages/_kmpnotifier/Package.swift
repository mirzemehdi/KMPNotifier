// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_kmpnotifier",
  platforms: [
    .iOS("16.0")
  ],
  products: [
    .library(
      name: "_kmpnotifier",
      type: .none,
      targets: ["_kmpnotifier"]
    )
  ],
  dependencies: [
  ],
  targets: [
    .target(
      name: "_kmpnotifier",
      dependencies: [
      ]
    )
  ]
)
