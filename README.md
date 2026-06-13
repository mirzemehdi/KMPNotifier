# KMPNotifier - Kotlin Multiplatform Notification
[![Build](https://github.com/mirzemehdi/KMPNotifier/actions/workflows/build.yml/badge.svg)](https://github.com/mirzemehdi/KMPNotifier/actions/workflows/build.yml) 
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.0-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mirzemehdi/kmpnotifier-core?color=blue)](https://search.maven.org/search?q=g:io.github.mirzemehdi)

![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat)
![badge-desktop](https://img.shields.io/badge/platform-desktop-3474eb.svg?style=flat)
![badge-js](https://img.shields.io/badge/platform-js-fcba03.svg?style=flat)
![badge-wasm](https://img.shields.io/badge/platform-wasm-331f06.svg?style=flat)

[![Android Weekly badge](https://androidweekly.net/issues/issue-632/badge)](https://androidweekly.net/issues/issue-632)
[![Android Weekly badge](https://androidweekly.net/issues/issue-599/badge)](https://androidweekly.net/issues/issue-599)


Simple and easy to use Kotlin Multiplatform Notification library: local notifications targeting android, iOS, desktop and web (js and wasm), and push notifications (Firebase Cloud Messaging) targeting android and iOS.  
This library is used in [FindTravelNow](https://github.com/mirzemehdi/FindTravelNow-KMM/) production KMP project.
You can check out [Documentation](https://mirzemehdi.github.io/KMPNotifier) for full library api information.  

![kmpnotifier](https://github.com/user-attachments/assets/a0f38159-b31d-4a47-97a7-cc230e15d30b)



**_Related Blog Posts_**  
[KMPNotifier Update: Web, Desktop, and New Features for Kotlin Multiplatform Notifications](https://proandroiddev.com/kmpnotifier-update-web-desktop-and-new-features-for-kotlin-multiplatform-notifications-529b489f5d9c)  
[How to implement Push Notifications in Kotlin Multiplatform](https://proandroiddev.com/how-to-implement-push-notification-in-kotlin-multiplatform-5006ff20f76c)  


## Features
  - 🔔 Local notifications (android, ios, desktop, js and wasm)
  - ⏰ Scheduled notifications (android and ios)
  - 🎬 Action buttons, with optional inline text input (android and ios)
  - 🖼️ Rich notifications with images, from URL or local file (android and ios)
  - 🔗 Click & payload delivery for deep linking
  - ☁️ Push notifications (Firebase Cloud Messaging) (android and ios only)
  - 📱 Multiplatform (android, iOS, desktop and web (js and wasm))
  - 📦 Modular: use local notifications without pulling in Firebase

### Feature / platform support

| Feature | Android | iOS | Desktop | JS / wasmJs |
|---|:---:|:---:|:---:|:---:|
| Local notification (title/body/payload) | ✅ | ✅ | ✅ | ✅ |
| Remove / remove all | ✅ | ✅ | ✅ | ✅ |
| Scheduled notification (`scheduledAt`) | ✅ | ✅ | ⛔️ shows now | ⛔️ shows now |
| Image (`NotificationImage.Url` / `.File`) | ✅ | ✅ | ⛔️ | ⛔️ |
| Action buttons | ✅ | ✅ | ⛔️ | ⛔️ |
| Action with text input | ✅ | ✅ | ⛔️ | ⛔️ |
| Click & action listener | ✅ | ✅ | ✅ | ✅ |
| Custom sound | ✅ channel | ✅ bundle | ⛔️ | ⛔️ |
| Push notification (FCM) | ✅ | ✅ | ⛔️ no-op mock | ⛔️ no-op mock |

`⛔️ shows now` = the call is accepted but the notification is shown immediately (the schedule is ignored). `no-op mock` = API exists and compiles, token is `null`, calls do nothing — so shared code needs no expect/actual.

## Modules

Since 2.0.0 the library is split into focused modules:

| Artifact | Use it for | Targets |
|---|---|---|
| `kmpnotifier-core` | shared core (configuration, permissions, events) — pulled in automatically; export it in the iOS framework | android, ios, jvm, js, wasmJs |
| `kmpnotifier-local` | local notifications (no Firebase) | android, ios, jvm, js, wasmJs |
| `kmpnotifier-push-firebase` | Firebase push (includes local; no-op mock on desktop/web) | android, ios, jvm, js, wasmJs |
| `kmpnotifier` | deprecated 1.x compatibility umbrella (includes everything) | all |

Upgrading from 1.x? Your code keeps working — see [MIGRATION.md](MIGRATION.md) for the
deprecation mapping and [CHANGELOG.md](CHANGELOG.md) for what changed.

## Installation

For push notifications you need the basic Firebase setup following the official guideline (initializing project in Firebase, adding `google-services.json` to android, `GoogleService-Info.plist` to iOS). Local-only usage needs no Firebase setup at all.

## Minimum Requirements

- **Kotlin:** `2.4.0+` (the library is built with Kotlin 2.4 and consumes Firebase via Swift Package Manager)
- **Android:** `minSdkVersion 23`
- **iOS:** `iOS 16.0` for push (`kmpnotifier-push-firebase`); local-only modules work on lower targets


### Gradle Setup
KMPNotifier is available on Maven Central. In your root project `build.gradle.kts` file (or `settings.gradle` file) add `mavenCentral()` to repositories. If you use push notifications, also add the `google-services` plugin.

```kotlin
repositories { 
  mavenCentral()
}
```

Then add the dependency in your shared module. Latest version: [![Maven Central](https://img.shields.io/maven-central/v/io.github.mirzemehdi/kmpnotifier-core?color=blue)](https://search.maven.org/search?q=g:io.github.mirzemehdi). In the iOS framework part, export the modules as well.

```kotlin
sourceSets {
  commonMain.dependencies {
    // Local notifications (all targets, no Firebase):
    api("io.github.mirzemehdi:kmpnotifier-local:<version>")
    // Firebase push (delivers on android/ios; no-op mock on desktop and web):
    api("io.github.mirzemehdi:kmpnotifier-push-firebase:<version>")
  }
}

listOf(iosX64(),iosArm64(),iosSimulatorArm64()).forEach { iosTarget ->
  iosTarget.binaries.framework {
    export("io.github.mirzemehdi:kmpnotifier-core:<version>")
    export("io.github.mirzemehdi:kmpnotifier-local:<version>")
    export("io.github.mirzemehdi:kmpnotifier-push-firebase:<version>") // if using push
    ...
  }
}
```

If you use push notifications, apply the `google-services` plugin in your androidApp `build.gradle.kts` file:
```kotlin
plugins {
  id("com.android.application")
  id("com.google.gms.google-services")
}
```


### Platform Setup
On application start you initialize the library once with the platform configuration and the capabilities (extensions) you want:

```kotlin 
// Local notifications only:
KMPNotifier.initialize(configuration, LocalNotifications)

// Local + Firebase push (android/ios; FirebasePush installs LocalNotifications automatically):
KMPNotifier.initialize(configuration, FirebasePush)
```

> **Note:** on iOS, `initialize` must be called from the **main thread** — the notification delegate is installed during init and is required to receive cold-start clicks. Calling `initialize` again is a no-op for the configuration, but it does install any extension not yet installed.

<details>
  <summary>Android</summary>

  ### Android Setup
 ```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        /**
         * By default showPushNotification value is true.
         * When set showPushNotification to false foreground push notification will not be shown to user.
         * You can still get notification content using a PushListener.
         */
        KMPNotifier.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.ic_launcher_foreground,
                showPushNotification = true,
                // optional — customize the notification channel (see "Notification channel & sound"):
                notificationChannelData = NotificationPlatformConfiguration.Android.NotificationChannelData(),
            ),
            FirebasePush, // omit for local-only usage (pass LocalNotifications instead)
        )
    }
}
```

Starting from Android 13 (API Level 33) you need to request the runtime `POST_NOTIFICATIONS` permission from an Activity. See [Asking notification permission](#asking-notification-permission).
  
  
</details>

<details>
  <summary>iOS</summary>

  ### iOS Setup
  For push notifications, add the `firebase-ios-sdk` Swift package (FirebaseMessaging product, version **12.14.0 exact** — the version the library is built against) to your iOS app in Xcode (File → Add Package Dependencies), call FirebaseApp initialization, and set the apnsToken as below. The library itself links Firebase through Swift Package Manager — no CocoaPods setup is needed. Don't forget to add Push Notifications and Background Modes (Remote Notifications) signing capability in Xcode. For local-only usage, skip everything Firebase-related and pass `LocalNotifications` instead of `FirebasePush`.

```swift
import SwiftUI
import shared
import FirebaseCore
import FirebaseMessaging

class AppDelegate: NSObject, UIApplicationDelegate {

  func application(_ application: UIApplication,
                   didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {

      FirebaseApp.configure() //important

      KMPNotifier.shared.initialize(
          configuration: NotificationPlatformConfigurationIos(
              showPushNotification: true,
              askNotificationPermissionOnStart: true,
              notificationSoundName: nil
          ),
          extensions: [FirebasePush.shared]
      )

    return true
  }

  func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
  }
    
}

@main
struct iOSApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}


```


 
</details>

<details>
  <summary>Desktop</summary>

### Desktop Setup
You need to put notification icon into resources/common folder. For more information:  
[Compose Desktop Resources](https://github.com/JetBrains/compose-multiplatform/blob/master/tutorials/Native_distributions_and_local_execution/README.md#packaging-resources)
 ```kotlin
fun main() = application {

    KMPNotifier.initialize(
        NotificationPlatformConfiguration.Desktop(
            showPushNotification = true,
            notificationIconPath = composeDesktopResourcesPath() + File.separator + "ic_notification.png"
        ),
        LocalNotifications,
    )
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMPNotifier Desktop",
    ) {
        println("Desktop app is started")
        App()

    }
}
```  

</details>

<details>
  <summary>Web</summary>

  
### Web Setup (Js and Wasm)
On application start initialize it using Web configuration
 ```kotlin
fun main()  {

    KMPNotifier.initialize(
        NotificationPlatformConfiguration.Web(
            askNotificationPermissionOnStart = true,
            notificationIconPath = null
        ),
        LocalNotifications,
    )
    
}
```
**Note:**
If you are using mac make sure you also allow notifications for browser from system settings in order to see web notifications.  




</details>

## Usage

You access the two notifiers through the `KMPNotifier` facade:

- `KMPNotifier.localNotifier` — local notifications (all platforms).
- `KMPNotifier.firebasePushNotifier` — push token / topic management (real on android/ios, no-op mock elsewhere).

> All snippets below need the library to be initialized first (see [Platform Setup](#platform-setup)). Accessing a notifier before `initialize` throws `IllegalStateException`. Use `KMPNotifier.isInitialized` to guard if needed.

### Local Notifications

Local notifications are supported on all targets. The richest entry point is the `notify { }` builder DSL.

#### Send a notification (builder DSL)

```kotlin
KMPNotifier.localNotifier.notify {
  id = Random.nextInt(0, Int.MAX_VALUE)   // omit to auto-generate; reuse the id to update/remove later
  title = "Title from KMPNotifier"
  body = "Body message from KMPNotifier"
  payloadData = mapOf(
    Notifier.KEY_URL to "https://github.com/mirzemehdi/KMPNotifier/",
    "extraKey" to "randomValue"
  )
  image = NotificationImage.Url("https://github.com/user-attachments/assets/a0f38159-b31d-4a47-97a7-cc230e15d30b")
}
```

Every builder property:

| Property | Type | Default | Notes |
|---|---|---|---|
| `id` | `Int` | random non-negative | Reuse to update or remove the notification later. |
| `title` | `String` | `""` | |
| `body` | `String` | `""` | |
| `payloadData` | `Map<String, String>` | `{}` | Delivered back on click and action events. |
| `image` | `NotificationImage?` | `null` | `Url` or `File`; android & ios only. |
| `actions` | `List<NotificationAction>` | `[]` | Action buttons; android & ios only. |
| `scheduledAt` | `Long` | `0L` | Epoch millis to fire; `0` shows immediately. android & ios only. |

There is also a `payload { }` sub-DSL if you prefer building the map inline:

```kotlin
KMPNotifier.localNotifier.notify {
  title = "Order shipped"
  body = "Tap to track"
  payload {
    put(Notifier.KEY_URL, "myapp://orders/42")
    put("orderId", "42")
  }
}
```

#### Send quickly (without the DSL)

```kotlin
val notifier = KMPNotifier.localNotifier

// auto id, returns the generated id:
val id: Int = notifier.notify(title = "Hi", body = "Quick notification")

// your own id (reuse it to update/remove):
notifier.notify(id = 100, title = "Hi", body = "Quick notification")
```

#### Schedule a notification

Set `scheduledAt` to the **epoch milliseconds** at which the notification should fire. Supported on **Android** (via `AlarmManager`) and **iOS** (via `UNTimeIntervalNotificationTrigger`). On desktop and web the value is ignored and the notification shows immediately.

```kotlin
// fire 1 minute from now
KMPNotifier.localNotifier.notify {
  id = 777
  title = "Reminder"
  body = "Stand up and stretch 🧘"
  scheduledAt = Clock.System.now().toEpochMilliseconds() + 60_000  // kotlinx-datetime
}
```

Cancel a scheduled (not-yet-fired) notification the same way you remove a shown one — `remove(id)` cancels the pending alarm too:

```kotlin
KMPNotifier.localNotifier.remove(777)
```

> **Android exact alarms:** the library uses an exact alarm and falls back to an inexact one if the OS denies exact scheduling. There is no repeating-notification API.

#### Action buttons (and inline text input)

Add `NotificationAction`s in the builder. Supported on **Android** and **iOS**.

```kotlin
KMPNotifier.localNotifier.notify {
  id = 200
  title = "New message"
  body = "Alex: are we still on for lunch?"
  actions = listOf(
    NotificationAction(id = "OPEN", title = "Open"),
    NotificationAction(
      id = "REPLY",
      title = "Reply",
      allowsTextInput = true,
      inputLabel = "Type your reply…"
    ),
  )
}
```

`NotificationAction`:

| Field | Type | Default | Notes |
|---|---|---|---|
| `id` | `String` | — | The `actionId` you receive in `onAction`. |
| `title` | `String` | — | Button label. |
| `allowsTextInput` | `Boolean` | `false` | Shows an inline text field. |
| `inputLabel` | `String?` | `null` | Placeholder/label for the text field. |

When the user taps an action it is delivered to `onAction` (see below). If the action allows text input, the entered text arrives in the payload under the key `"remote_input"`.

#### Notification images

```kotlin
image = NotificationImage.Url("https://example.com/picture.png")  // requires internet permission
image = NotificationImage.File("/path/to/local/picture.png")      // app must be able to read the file
```

Rendered as a big picture on Android and as an attachment on iOS. Only `Url` and `File` exist (no resource variant).

#### Remove notifications

```kotlin
val notifier = KMPNotifier.localNotifier

notifier.remove(notificationId) // removes a shown notification (and cancels it if still scheduled)
notifier.removeAll()            // removes all notifications
```

#### Notification click & action listener

Clicks and action buttons are **shared** events — they fire for both local and push notifications. Register a `KMPNotifier.Listener`:

```kotlin
KMPNotifier.addListener(object : KMPNotifier.Listener {
  override fun onNotificationClicked(data: PayloadData) {
    println("Notification clicked, payload: $data") // PayloadData = Map<String, *>
  }

  override fun onAction(actionId: String, notificationId: Int, payload: PayloadData) {
    println("Action $actionId on notification $notificationId, payload: $payload")
    if (actionId == "REPLY") {
      val text = payload["remote_input"] as? String  // inline text-input result
    }
  }
})
```

Both callbacks have default empty bodies, so override only what you need. Manage listeners with `addListener` / `removeListener` / `setListener(null)` (the last removes all).

### Push Notifications

Push notifications are delivered on **Android and iOS** via the `kmpnotifier-push-firebase` module. The module compiles on every target — on desktop and web the notifier is a no-op mock (token is `null`), so shared code needs no expect/actual.

#### Listen for push events

Push-specific events (token updates, push payloads) use `PushListener`. Register it via `KMPNotifier.addPushListener`:

```kotlin
KMPNotifier.addPushListener(object : PushListener {
  override fun onNewToken(token: String) {
    println("onNewToken: $token") // update the user's token on your server if needed
  }

  override fun onPushNotification(title: String?, body: String?) {
    println("Push received — title: $title, body: $body")
  }

  override fun onPayloadData(data: PayloadData) {
    println("Push payloadData: $data") // PayloadData is a typeAlias for Map<String, *>
  }

  override fun onPushNotificationWithPayloadData(title: String?, body: String?, data: PayloadData) {
    println("Push received — title: $title, body: $body, payloadData: $data")
  }
})
```

All callbacks default to empty, so override only the ones you use. Manage with `addPushListener` / `removePushListener` / `setPushListener(null)`.

To receive the payload data correctly you must call the platform hooks below.

##### Android
Call `KMPNotifier.onCreateOrOnNewIntent(intent)` in your launcher Activity's `onCreate` and `onNewIntent`:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    KMPNotifier.onCreateOrOnNewIntent(intent)
    ...
}

override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    KMPNotifier.onCreateOrOnNewIntent(intent)
}
```

##### iOS
Call `KMPNotifier.onApplicationDidReceiveRemoteNotification(userInfo:)` in your app's `didReceiveRemoteNotification`:

```swift
 func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any]) async -> UIBackgroundFetchResult {
      KMPNotifier.shared.onApplicationDidReceiveRemoteNotification(userInfo: userInfo)
      return UIBackgroundFetchResult.newData
 }
```  

#### Token & topic management

```kotlin
// All push notifier functions are suspend — call them from a coroutine.
KMPNotifier.firebasePushNotifier.getToken()                       // current push token (null on desktop/web)
KMPNotifier.firebasePushNotifier.deleteMyToken()                  // delete the token, e.g. on logout
KMPNotifier.firebasePushNotifier.subscribeToTopic("new_users")
KMPNotifier.firebasePushNotifier.unSubscribeFromTopic("new_users")
```

### Permissions

`KMPNotifier.permissionUtil` exposes callback-based notification-permission helpers on every platform:

```kotlin
val permissionUtil = KMPNotifier.permissionUtil

permissionUtil.hasNotificationPermission { granted -> /* ... */ }
permissionUtil.askNotificationPermission { granted -> /* ... */ }
```

On iOS and web the configuration's `askNotificationPermissionOnStart` flag asks for permission automatically at init.

#### Asking notification permission (Android 13+)

`POST_NOTIFICATIONS` (API 33+) must be requested from an `Activity`. The library provides a `ComponentActivity` extension that wires `ActivityResultContracts.RequestPermission` for you. On API < 33 it reports `true` immediately.

```kotlin
class MainActivity : ComponentActivity() {
    private val permissionUtil by permissionUtil() // com.mmk.kmpnotifier.permission.permissionUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionUtil.askNotificationPermission { granted ->
            // optionally react to the result
        }
    }
}
```

### Notification channel & sound

- **Android:** the channel is configured through `NotificationPlatformConfiguration.Android.NotificationChannelData(id, name, description, soundUri)`. Pass a `soundUri` (as a `String`) to set a custom sound. A single high-importance channel is created automatically on first `notify`.
- **iOS:** set `notificationSoundName` in `NotificationPlatformConfiguration.Ios` to a sound file added to your app's *Copy Bundle Resources*; `null` uses the default sound.
- **Desktop / web:** no custom-sound API.

For more detail and examples:
- Custom notification sound: https://github.com/mirzemehdi/KMPNotifier/pull/61#issuecomment-2275850021
- Setting Intent data on Android (deep links): https://github.com/mirzemehdi/KMPNotifier/pull/60#issue-2454489089
- Manually asking notification permission: https://github.com/mirzemehdi/KMPNotifier/pull/27#issuecomment-2083639907

### Logging

To see the library's internal logs, set a logger:

```kotlin
KMPNotifier.setLogger { message ->
    println(message)
}
```

## Migrating from 1.x

The old `NotifierManager` API (from the `kmpnotifier` artifact) keeps working in 2.x —
it is deprecated and forwards to the new API. See [MIGRATION.md](MIGRATION.md).
