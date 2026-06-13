// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_kmpnotifier-push-firebase",
  platforms: [
    .iOS("16.0")
  ],
  products: [
    .library(
      name: "_kmpnotifier-push-firebase",
      type: .none,
      targets: ["_kmpnotifier-push-firebase"]
    )
  ],
  dependencies: [
    .package(
      url: "https://github.com/firebase/firebase-ios-sdk.git",
      exact: "12.14.0"
    )
  ],
  targets: [
    .target(
      name: "_kmpnotifier-push-firebase",
      dependencies: [
        .product(
          name: "FirebaseMessaging",
          package: "firebase-ios-sdk"
        )
      ]
    )
  ]
)
