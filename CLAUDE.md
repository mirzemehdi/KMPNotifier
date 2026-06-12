# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

KMPNotifier is a Kotlin Multiplatform notification library (`io.github.mirzemehdi`) for local notifications (Android, iOS, desktop/JVM, JS, wasmJS) and Firebase push notifications (Android/iOS only). Gradle modules:

- `:kmpnotifier-core` — shared core: `KMPNotifier` facade, configuration, permissions, logger, manual dependency wiring, internal event hub. All targets.
- `:kmpnotifier-local` — local notifications (`LocalNotifications` extension, `Notifier` + platform impls). All targets. Depends on core.
- `:kmpnotifier-push-firebase` — Firebase push (`FirebasePush` extension, `PushListener`). All targets — Firebase delivery on android/iOS, shared no-op mock elsewhere (1.x parity). Depends on local. Declares the Firebase iOS dependency via SwiftPM (`swiftPMDependencies`).
- `:kmpnotifier` — deprecated compatibility umbrella: old `NotifierManager` API forwarding to the new API; `api()`-depends on all modules. Removal planned for 3.0.0.
- `:sample` — Compose Multiplatform demo app (excluded from API validation and publishing); its iOS host project lives in `iosApp/`

All four library modules apply the `kmpnotifier.library` convention plugin from the `build-logic/` included build (targets, android block, shared test deps, Maven Central publishing). Module build files declare only: dependencies, `android.namespace`, POM name/description, and module-specific blocks (push: `swiftPMDependencies`).

## Commands

JDK 17+ required (e.g. `JAVA_HOME=$(/usr/libexec/java_home -v 17)`).

```sh
./gradlew apiCheck                                 # binary compatibility check (runs first in CI)
./gradlew apiDump                                  # regenerate <module>/api/* after intentional public API changes (run on macOS)
./gradlew testDebugUnitTest testReleaseUnitTest    # Android unit tests (incl. Robolectric), all modules
./gradlew jvmTest jsNodeTest                       # desktop + js tests
./gradlew iosX64Test iosSimulatorArm64Test         # iOS tests (macOS only)
./gradlew :kmpnotifier-core:jvmTest --tests "com.mmk.kmpnotifier.SomeTest"  # single test class
./gradlew dokkaHtmlMultiModule                     # generate docs (published to GitHub Pages on release)
./gradlew publishToMavenLocal                      # local publish (signing is skipped for this task)
./gradlew kotlinUpgradeYarnLock                    # after js dependency changes (yarn.lock check fails otherwise)
```

Sample app:

```sh
./gradlew :sample:installDebug                     # Android
./gradlew :sample:run                              # Desktop
./gradlew :sample:wasmJsBrowserDevelopmentRun      # Web (wasm)
```

`firebase_push_notification_sender.py` sends test FCM pushes (requires a Firebase service-account JSON and project ID filled in at the top of the script).

## Releasing

Version is `kmpNotifierVersion` in `gradle.properties` (all four artifacts share it). Pushing a `v*` tag triggers CI release: Dokka docs to GitHub Pages + `publishAndReleaseToMavenCentral`. CI runs `apiCheck` before any build, so a public API change without a committed `apiDump` fails the pipeline. artifactId == Gradle project name (set by the convention plugin) — renaming a module directory changes the published coordinates.

## Architecture

**Facade + pluggable extensions.** `KMPNotifier.initialize(configuration, vararg extensions)` (core) wires dependencies manually (no DI framework) and installs `KMPNotifierExtension`s. `LocalNotifications` (local module) registers the platform `Notifier`; `FirebasePush` (push module) registers the Firebase `PushNotifier` and declares `dependsOn = [LocalNotifications]`, so users pass only `FirebasePush`. Accessors: `KMPNotifier.localNotifier` / `LocalNotifications.notifier`, `KMPNotifier.firebasePushNotifier` / `FirebasePush.notifier` (firebase delivery android/ios; no-op mock on jvm/js/wasm).

**Cross-module wiring via `@InternalKMPNotifierApi`** (`com.mmk.kmpnotifier.internal`, opt-in ERROR level):
- `NotifierInternals` — registries (local notifier as `Any` since `Notifier` lives downstream; push notifier), configuration/permission access, `resetForTests()`.
- `NotifierEventHub` — single fan-out point. Shared events (click, action) → `KMPNotifier.Listener`s + internal listeners (umbrella bridge). Push events (token, payloads) → `PushEventSink`s (push module's `PushListener` forwarding; umbrella bridge). Listener/sink registries are sets — re-registration is idempotent.
- Event sources: Android `MyFirebaseMessagingService` (push), `NotificationReceiver` (local), `KMPNotifier.onCreateOrOnNewIntent` (core androidMain — inspects intent extras for firebase markers in `Constants`); iOS `IosNotifier.NotificationDelegate` (local — handles BOTH local and push content via `gcm.message_id` marker, no Firebase types) and `KMPNotifier.onApplicationDidReceiveRemoteNotification` (push iosMain, KMPNotifier-receiver extension).

**Listener split (deliberate API decision):** `KMPNotifier.Listener` carries only shared events (`onNotificationClicked`, `onAction` — fired for both local and push notifications). Push-only callbacks (`onNewToken`, `onPayloadData`, `onPushNotification`, `onPushNotificationWithPayloadData`) live in `PushListener` in the push module. Don't add push concerns to core/local public API.

**iOS delegate ownership:** `UNUserNotificationCenter.delegate` is installed by `kmpnotifier-local` during `LocalNotifications.install` and retained strongly in `IosDelegateHolder` (the ObjC delegate property is weak — without the holder, callbacks silently die). The Firebase impl only sets the `FIRMessaging` delegate. Initialize must be called from the main thread on iOS (documented behavior).

**Manual dependency wiring** (deliberate decision — no DI framework on the runtime classpath). `LibDependencyInitializer` (core di/) holds a `NotifierDependencies(configuration, permissionUtil)` created once at initialize; per-platform `internal actual val platform` + `internal actual fun createPermissionUtil()` provide the platform pieces (android `applicationContext` captured via androidx.startup `ContextInitializer`).

**Umbrella compatibility layer** (`:kmpnotifier`): `NotifierManager` keeps the exact 1.x signatures; `NotifierManagerImpl` is a bridge object implementing `KMPNotifier.Listener + PushEventSink`, registered via the hub's internal-listener channel (immune to `KMPNotifier.setListener(null)`), forwarding all six callbacks to legacy `NotifierManager.Listener`s. Old `initialize` installs `defaultExtensions()` (expect/actual: local+push on android/ios, local elsewhere). Old `getPushNotifier()` falls back to `EmptyPushNotifierImpl` on jvm/js/wasm (core holds the `PushNotifier` abstraction for exactly this reason).

**Android manifests:** core declares POST_NOTIFICATIONS + the startup provider; local declares `NotificationReceiver`; push declares `MyFirebaseMessagingService` with its FULLY QUALIFIED class name (module namespaces differ — a relative `.firebase.` name would resolve wrongly during manifest merge). Class FQNs are unchanged from 1.x, so merged app manifests are equivalent.

**SwiftPM (no CocoaPods):** only `:kmpnotifier-push-firebase` declares `swiftPMDependencies { swiftPackage(firebase-ios-sdk, exact 12.1.0, products [FirebaseMessaging], discoverClangModulesImplicitly = false) }`. Kotlin imports use `swiftPMImport.io.github.mirzemehdi.kmpnotifier.push.firebase.*` (declaring module's group+name). Never declare the same package's cinterop in two modules of one dependency graph — duplicate bindings fail the link. The iosApp consumes Firebase via Xcode SPM (exact 12.1.0 — keep versions in sync) and needs `:sample:integrateLinkagePackage` re-run when products change (generates `iosApp/KotlinMultiplatformLinkedPackage/`).

**JS and wasmJs**: near-identical duplicated sources (`WebConsoleNotifier`, `WebPermissionUtilImpl`, `PlatformModule.web.kt`) — changes to one usually must be mirrored in the other.

## Constraints

- `explicitApi()` on all library modules; public declarations need explicit visibility and return types.
- BCV (klib included) covers all four published modules; any public API change requires `./gradlew apiDump` (macOS) and committing the `*/api/` files.
- FQNs of symbols moved between modules must stay `com.mmk.kmpnotifier.*` (binary compatibility with 1.x relies on it).
- Tests: NO commonTest may call `initialize()` — iOS test binaries crash on UNUserNotificationCenter/FIRMessaging/Dispatchers.Main. Initialization tests live in jvmTest (Desktop config) and androidUnitTest (Robolectric, `@Config(sdk = [34])`). Use `NotifierInternals.resetForTests()` between tests.
- Avoid pure data-holder tests (data classes, plain defaults) — they were pruned deliberately; test behavior, not language features.
- Min versions: Kotlin 2.4.0 (SwiftPM import support), Android SDK 23, iOS 16.0 for the push module, JVM toolchain 17.
- Keep KDoc on public API — Dokka output is the published documentation site. Keep `CHANGELOG.md`/`MIGRATION.md` in sync with API changes.
