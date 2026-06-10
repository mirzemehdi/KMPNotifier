// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "KotlinMultiplatformLinkedPackage",
  platforms: [
    .iOS("16.0")
  ],
  products: [
    .library(
      name: "KotlinMultiplatformLinkedPackage",
      type: .none,
      targets: ["KotlinMultiplatformLinkedPackage"]
    )
  ],
  dependencies: [
    .package(path: "subpackages/_kmpnotifier"),
    .package(path: "subpackages/_kmpnotifier-push-firebase"),
    .package(path: "subpackages/_kmpnotifier_push_firebase"),
    .package(path: "subpackages/_sample")
  ],
  targets: [
    .target(
      name: "KotlinMultiplatformLinkedPackage",
      dependencies: [
        .product(name: "_kmpnotifier", package: "_kmpnotifier"),
        .product(name: "_kmpnotifier-push-firebase", package: "_kmpnotifier-push-firebase"),
        .product(name: "_kmpnotifier_push_firebase", package: "_kmpnotifier_push_firebase"),
        .product(name: "_sample", package: "_sample")
      ]
    )
  ]
)
