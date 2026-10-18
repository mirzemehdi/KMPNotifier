# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed

- Android: scheduled local notifications (`scheduledAt` in the future) are now actually
  displayed when the alarm fires. Previously `NotificationReceiver` only emitted the
  `EVENT_SCHEDULED_NOTIFICATION_FIRED_INTERNAL` action and never posted the notification.
  The title, body, payload, image, actions and Android configuration are stored in the alarm
  intent, so the notification is posted even when the alarm fires in a cold process before
  `KMPNotifier.initialize`. The `onAction` event is still emitted after posting — apps that
  re-posted the notification from that callback as a workaround must remove that code
  (on both Android and iOS, which now emits the event too).
- Android: `remove(id)` now cancels a pending scheduled alarm. The cancel intent did not match
  the scheduled one, so the alarm was never found.
- Android: relative `scheduledAt` values (milliseconds from now) are now scheduled at the
  correct time instead of firing immediately.
- Android: on API 31+ without `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM`, scheduling checks
  `AlarmManager.canScheduleExactAlarms()` and uses an inexact `setAndAllowWhileIdle` alarm
  instead of throwing and logging a `SecurityException`.
- iOS: the scheduled-fired `onAction` event (`EVENT_SCHEDULED_NOTIFICATION_FIRED_INTERNAL`) is
  now emitted when a scheduled local notification is presented while the app is in the
  foreground, matching Android. (iOS shows scheduled notifications itself, so the event cannot
  fire while the app is in the background.)
- iOS: scheduled notifications use `UNCalendarNotificationTrigger` for the absolute fire date
  instead of an interval computed at scheduling time; immediate notifications are delivered
  without a 1-second delay.
- iOS: action-button categories are registered before the notification request is added (the
  buttons could be missing on immediate notifications), and replace a previous category for
  the same id instead of accumulating duplicates.
- iOS: `NotificationImage.File` images are copied before attaching — the system moves
  attachment files into its own store, so the caller's file was deleted.
- iOS: image attachments get the correct file extension (PNG / JPEG / GIF detected from the
  data) instead of always `.jpg`, so non-JPEG images are no longer rejected.

## [2.0.1] - 2026-08-14

### Changed

- `kmpnotifier-push-firebase` now declares its `firebase-ios-sdk` SwiftPM dependency as a
  range (`from: 12.14.0`, up to the next major) instead of an exact pin. Apps that also
  depend on `firebase-ios-sdk` directly can resolve any 12.x version ≥ 12.14.0 (e.g. to use
  the FID registration API added in 12.16.0) without a dependency-resolution conflict.
  Thanks @utamori! (#177, #178)
- The deprecated `kmpnotifier` umbrella artifact is no longer published; 2.0.0 is its final
  version. It remains a compatibility bridge for 1.x upgraders — new code should depend on
  `kmpnotifier-local` / `kmpnotifier-push-firebase` directly.

### Security

- All npm advisories in the Kotlin/JS build toolchain lock resolved (49 Dependabot alerts,
  13 packages — dev/build tooling only, nothing shipped in published artifacts). (#179)

## [2.0.0] - 2026-06-12

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
- Scheduled local notifications via the `scheduledAt` (epoch millis) builder property — delivered on android (`AlarmManager`) and iOS (`UNTimeIntervalNotificationTrigger`); shown immediately on desktop/web. `remove(id)` cancels a pending schedule.
- Notification action buttons via `NotificationAction` (the `actions` builder property), including inline text input (`allowsTextInput` / `inputLabel`; entered text delivered under the `remote_input` payload key). Android and iOS. Triggered actions arrive in `KMPNotifier.Listener.onAction`.
- Builder DSL (`KMPNotifier.localNotifier.notify { }`) with `id`, `title`, `body`, `payloadData` (and a `payload { }` sub-DSL), `image`, `actions`, `scheduledAt`.
- `CHANGELOG.md` and `MIGRATION.md`.
- Cross-platform test suite (jvm, android, ios, js).

### Changed

- Built with AGP 9 (`com.android.kotlin.multiplatform.library`). The android target is now
  single-variant: the `-android-debug` artifacts published by 1.x no longer exist; consumers
  always get the release variant (standard for KMP libraries).
- The library no longer depends on Koin (or any DI framework) at runtime — dependencies are
  wired manually inside the core module. One fewer transitive dependency for consumers.

- `io.github.mirzemehdi:kmpnotifier` is now a compatibility umbrella that depends on the new
  modules. Existing code keeps compiling and working unchanged.
- iOS: Firebase is now consumed through Swift Package Manager instead of the CocoaPods Gradle
  plugin (Kotlin `swiftPMDependencies`, firebase-ios-sdk 12.14.0). Kotlin is updated to 2.4.0
  and the iOS minimum deployment target is now 16.0 (was 15.4).
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
