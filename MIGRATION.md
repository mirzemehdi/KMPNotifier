# Migrating to KMPNotifier 2.0

KMPNotifier 2.0 splits the library into focused modules and introduces a new entry point,
`KMPNotifier`. The old `NotifierManager` API keeps working in 2.x (it is deprecated and
forwards to the new API), so you can upgrade first and migrate incrementally.
Removal of the deprecated API is planned for 3.0.0.

## Artifacts

| Artifact | Contents | Targets |
|---|---|---|
| `kmpnotifier-core` | configuration, permissions, logging, shared events | android, ios, jvm, js, wasmJs |
| `kmpnotifier-local` | local notifications (depends on core) | android, ios, jvm, js, wasmJs |
| `kmpnotifier-push-firebase` | Firebase Cloud Messaging push (depends on local) | android, ios |
| `kmpnotifier` | deprecated umbrella, pulls in all of the above | android, ios, jvm, js, wasmJs |

Pick your dependency:

```kotlin
commonMain.dependencies {
    // Local notifications only — no Firebase anywhere:
    api("io.github.mirzemehdi:kmpnotifier-local:2.0.0")
}

// Push (android/ios): add the push module too
androidMain.dependencies {
    api("io.github.mirzemehdi:kmpnotifier-push-firebase:2.0.0")
}
iosMain.dependencies {
    api("io.github.mirzemehdi:kmpnotifier-push-firebase:2.0.0")
}
```

Keeping `io.github.mirzemehdi:kmpnotifier:2.0.0` also works — it brings in everything,
exactly like 1.x.

## Initialization

Initialization now takes the capabilities (extensions) you want:

```kotlin
// Before (still works, deprecated):
NotifierManager.initialize(configuration)

// After — local notifications only:
KMPNotifier.initialize(configuration, LocalNotifications)

// After — local + Firebase push (FirebasePush installs LocalNotifications automatically):
KMPNotifier.initialize(configuration, FirebasePush)

// Android overload (when androidx-startup is disabled):
KMPNotifier.initialize(context, configuration, FirebasePush)
```

> **iOS note:** pass the extensions at `initialize` time (called from
> `didFinishLaunchingWithOptions`). The notification-center delegate is installed during
> initialization; installing it later can miss cold-start notification clicks.

## API mapping

| 1.x | 2.0 |
|---|---|
| `NotifierManager.initialize(config)` | `KMPNotifier.initialize(config, LocalNotifications)` / `KMPNotifier.initialize(config, FirebasePush)` |
| `NotifierManager.initialize(context, config)` (android) | `KMPNotifier.initialize(context, config, ...extensions)` |
| `NotifierManager.getLocalNotifier()` | `KMPNotifier.localNotifier` (or `LocalNotifications.notifier`) |
| `NotifierManager.getPushNotifier()` | `KMPNotifier.firebasePushNotifier` (or `FirebasePush.notifier`) |
| `NotifierManager.getPermissionUtil()` | `KMPNotifier.permissionUtil` |
| `NotifierManager.setLogger { }` | `KMPNotifier.setLogger { }` |
| `NotifierManager.onCreateOrOnNewIntent(intent)` (android) | `KMPNotifier.onCreateOrOnNewIntent(intent)` |
| `NotifierManager.onApplicationDidReceiveRemoteNotification(userInfo)` (ios) | `FirebasePush.onApplicationDidReceiveRemoteNotification(userInfo)` |
| `NotifierManager.onUserNotification(content)` (ios) | handled automatically by the delegate; `FirebasePush.onUserNotification(content)` if called manually |
| `NotifierManager.onNotificationClicked(content)` (ios) | `KMPNotifier.onNotificationClicked(content)` |

### Swift call sites

```swift
// Before:
NotifierManager.shared.onApplicationDidReceiveRemoteNotification(userInfo: userInfo)

// After:
FirebasePush.shared.onApplicationDidReceiveRemoteNotification(userInfo: userInfo)
```

From Swift, prefer the member accessors: `LocalNotifications.shared.notifier`,
`FirebasePush.shared.notifier`.

## Listeners

The single 6-method `NotifierManager.Listener` is split by concern:

- **Shared events** (fired for both local and push notifications) — `KMPNotifier.Listener`:
  `onNotificationClicked(data)`, `onAction(actionId, notificationId, payload)`.
  Register with `KMPNotifier.addListener(...)`.
- **Push events** — `PushListener` (in `kmpnotifier-push-firebase`):
  `onNewToken(token)`, `onPayloadData(data)`, `onPushNotification(title, body)`,
  `onPushNotificationWithPayloadData(title, body, data)`.
  Register with `FirebasePush.addListener(...)`.

```kotlin
// Before:
NotifierManager.addListener(object : NotifierManager.Listener {
    override fun onNewToken(token: String) { ... }
    override fun onNotificationClicked(data: PayloadData) { ... }
})

// After:
KMPNotifier.addListener(object : KMPNotifier.Listener {
    override fun onNotificationClicked(data: PayloadData) { ... }
})
FirebasePush.addListener(object : PushListener {
    override fun onNewToken(token: String) { ... }
})
```

> Avoid mixing old `NotifierManager` listeners and new listeners in the same app —
> both receive events, which can lead to double handling during a partial migration.

## Android manifest

No action needed. The Firebase messaging service moved to the `kmpnotifier-push-firebase`
manifest and the notification receiver / startup provider to `kmpnotifier-local` /
`kmpnotifier-core` — class names are unchanged, and the merged manifest of your app
is equivalent to 1.x.

## Behavior changes

- iOS: the notification-center delegate is installed by the local notifications module during
  `initialize` and retained strongly (previously it was installed by the Firebase initializer
  and held weakly — callbacks could silently stop). Local-only iOS setups no longer need
  Firebase for notification click handling.
- Desktop/web: `getPushNotifier()` on the deprecated API still returns a no-op push notifier.
  The new `KMPNotifier.firebasePushNotifier` accessor simply does not exist on these targets — push is now a
  compile-time capability of android/ios only.
