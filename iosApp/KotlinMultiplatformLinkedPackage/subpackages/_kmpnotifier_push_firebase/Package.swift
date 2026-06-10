// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_kmpnotifier_push_firebase",
  platforms: [
    .iOS("16.0")
  ],
  products: [
    .library(
      name: "_kmpnotifier_push_firebase",
      type: .none,
      targets: ["_kmpnotifier_push_firebase"]
    )
  ],
  dependencies: [
    .package(
      url: "https://github.com/firebase/firebase-ios-sdk.git",
      exact: "12.1.0"
    )
  ],
  targets: [
    .target(
      name: "_kmpnotifier_push_firebase",
      dependencies: [
        .product(
          name: "FirebaseMessaging",
          package: "firebase-ios-sdk"
        )
      ]
    )
  ]
)
