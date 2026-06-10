# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

KMPNotifier is a Kotlin Multiplatform library (`io.github.mirzemehdi:kmpnotifier`) for push notifications (Firebase Cloud Messaging, Android/iOS only) and local notifications (Android, iOS, desktop/JVM, JS, wasmJS). Two Gradle modules:

- `:kmpnotifier` — the published library
- `:sample` — Compose Multiplatform demo app (excluded from API validation and publishing); its iOS host project lives in `iosApp/`

## Commands

```sh
./gradlew apiCheck                                 # binary compatibility check (runs first in CI)
./gradlew apiDump                                  # regenerate kmpnotifier/api/* after intentional public API changes
./gradlew testDebugUnitTest testReleaseUnitTest    # Android unit tests
./gradlew iosX64Test iosSimulatorArm64Test         # iOS tests (macOS only)
./gradlew :kmpnotifier:testDebugUnitTest --tests "com.mmk.kmpnotifier.SomeTest"  # single test class
./gradlew dokkaHtmlMultiModule                     # generate docs (published to GitHub Pages on release)
./gradlew publishToMavenLocal                      # local publish (signing is skipped for this task)
```

Sample app:

```sh
./gradlew :sample:installDebug                     # Android
./gradlew :sample:run                              # Desktop
./gradlew :sample:wasmJsBrowserDevelopmentRun      # Web (wasm)
```

`firebase_push_notification_sender.py` sends test FCM pushes (requires a Firebase service-account JSON and project ID filled in at the top of the script).

## Releasing

Version is `kmpNotifierVersion` in `gradle.properties`. Pushing a `v*` tag triggers CI release: Dokka docs to GitHub Pages + `publishAndReleaseToMavenCentral`. CI runs `apiCheck` before any build, so a public API change without a committed `apiDump` fails the pipeline.

## Architecture

**Public API facade → internal impl → isolated Koin DI.** The entire public surface is the `NotifierManager` object in `commonMain` (plus `Notifier`, `PushNotifier`, `PermissionUtil` interfaces and `NotificationPlatformConfiguration`). It delegates to internal `NotifierManagerImpl`, which resolves dependencies from Koin.

**Isolated Koin container.** `LibDependencyInitializer` (di/) creates its own `koinApplication` — deliberately NOT `startKoin` — so the library never conflicts with a host app's Koin. `KMPKoinComponent` overrides `getKoin()` to point at this container. The user-supplied `NotificationPlatformConfiguration` is registered as a Koin single and consumed by platform implementations.

**expect/actual per platform via `platformModule`.** Each source set provides `internal actual val platformModule: Module` (di/PlatformModule.*.kt) binding that platform's `Notifier`, `PushNotifier`, `PermissionUtil`, and `Platform` marker:

- **Android**: `AndroidNotifier` + `NotificationChannelFactory`; `FirebasePushNotifierImpl`; `Context` obtained via androidx.startup (`ContextInitializer`) — no manual context passing. `MyFirebaseMessagingService` receives FCM messages; `NotificationReceiver` handles action buttons.
- **iOS**: `IosNotifier` (UNUserNotificationCenter); `FirebasePushNotifierImpl` via the CocoaPods `FirebaseMessaging` pod (configured in `kmpnotifier/build.gradle.kts` cocoapods block, `noPodspec()`, static framework).
- **JVM/desktop**: `DesktopNotifierFactory` picks `TrayNotifier` (java.awt.SystemTray) or falls back to `JOptionPaneNotifier`. Push notifier is `EmptyPushNotifierImpl`.
- **JS and wasmJs**: near-identical duplicated sources (`WebConsoleNotifier`, `WebPermissionUtilImpl`, `PlatformModule.web.kt`) — changes to one usually must be mirrored in the other. Push notifier is `EmptyPushNotifierImpl`.

**Push event flow.** Platform entry points (Android: `MyFirebaseMessagingService` and `NotifierManager.onCreateOrOnNewIntent(intent)` in extensions/; iOS: `onApplicationDidReceiveRemoteNotification` in extensions/) forward into `NotifierManagerImpl.onXxx(...)` methods, which fan out to registered `NotifierManager.Listener`s (token updates, payload data, notification clicks, action buttons).

## Constraints

- `explicitApi()` is enabled on the library: every public declaration needs an explicit visibility modifier and public functions need explicit return types.
- Binary compatibility validation includes klib targets; any public API change requires `./gradlew apiDump` and committing the updated `kmpnotifier/api/` files.
- Min versions: Android SDK 21, iOS deployment target 15.4 (cocoapods block), JVM toolchain 17.
- Keep KDoc on public API — Dokka output is the published documentation site.
