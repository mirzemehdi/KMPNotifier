# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - Unreleased

The library is split into focused modules so that local-notification users no longer
pull in Firebase. See [MIGRATION.md](MIGRATION.md) for a step-by-step guide.

### Added

- New artifacts:
  - `io.github.mirzemehdi:kmpnotifier-core` — shared core (configuration, permissions, logging, shared events). All targets.
  - `io.github.mirzemehdi:kmpnotifier-local` — local notifications, no Firebase dependency. All targets.
  - `io.github.mirzemehdi:kmpnotifier-push-firebase` — Firebase Cloud Messaging push. All targets: delivers on android/iOS, no-op mock on desktop/web (1.x parity).
- New entry point `KMPNotifier` with pluggable extensions:
  `KMPNotifier.initialize(configuration, LocalNotifications)` or
  `KMPNotifier.initialize(configuration, FirebasePush)` (FirebasePush installs LocalNotifications automatically).
- `KMPNotifier.localNotifier` / `LocalNotifications.notifier` — local notifier accessor.
- `KMPNotifier.firebasePushNotifier` / `FirebasePush.notifier` — push notifier accessor (push module only).
- `KMPNotifier.Listener` — shared events (notification clicks, action buttons) for both local and push notifications.
- `PushListener` + `KMPNotifier.addPushListener/removePushListener/setPushListener` (sugar over `FirebasePush.addListener/...`) — push-only events
  (token updates, push payloads), no longer part of the shared listener surface.
- `CHANGELOG.md` and `MIGRATION.md`.
- Cross-platform test suite (jvm, android, ios, js).

### Changed

- `io.github.mirzemehdi:kmpnotifier` is now a compatibility umbrella that depends on the new
  modules. Existing code keeps compiling and working unchanged.
- Android: the Firebase messaging service is declared by `kmpnotifier-push-firebase`;
  the notification receiver and startup provider are declared by `kmpnotifier-local`/`kmpnotifier-core`.
  The merged manifest of existing apps is equivalent — no action needed.

### Deprecated

- `NotifierManager` and its platform extension functions. They keep working in 2.x and forward
  to the new API; removal is planned for 3.0.0. Replace with `KMPNotifier`, `LocalNotifications`
  and `FirebasePush` (see MIGRATION.md).

### Fixed

- iOS: the `UNUserNotificationCenter` delegate is now owned by the local notifications module and
  retained strongly. Previously it was created inside the Firebase initializer and held only by a
  weak Objective-C reference, so notification callbacks could silently stop after garbage collection,
  and local-only setups depended on Firebase initialization for click handling.
