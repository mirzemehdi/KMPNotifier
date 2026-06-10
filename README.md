# KMPNotifier - Kotlin Multiplatform Notification
[![Build](https://github.com/mirzemehdi/KMPNotifier/actions/workflows/build.yml/badge.svg)](https://github.com/mirzemehdi/KMPNotifier/actions/workflows/build.yml) 
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.21-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
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
  - 🔔 Local Notification (android, ios, desktop, js and wasm targets)  
  - 🔔 Push Notification (Firebase Cloud Messaging) (android and ios only)  
  - 📱 Multiplatform (android, iOS, desktop and web (js and wasm))  
  - 📦 Modular: use local notifications without pulling in Firebase

## Modules

Since 2.0.0 the library is split into focused modules:

| Artifact | Use it for | Targets |
|---|---|---|
| `kmpnotifier-local` | local notifications (no Firebase) | android, ios, jvm, js, wasmJs |
| `kmpnotifier-push-firebase` | Firebase push (includes local) | android, ios |
| `kmpnotifier` | deprecated 1.x compatibility umbrella (includes everything) | all |

Upgrading from 1.x? Your code keeps working — see [MIGRATION.md](MIGRATION.md) for the
deprecation mapping and [CHANGELOG.md](CHANGELOG.md) for what changed.

## Installation

For push notifications you need the basic Firebase setup following the official guideline (initializing project in Firebase, adding `google-services.json` to android, `GoogleService-Info.plist` to iOS). Local-only usage needs no Firebase setup at all.

## Minimum Requirements

- **Android:** `minSdkVersion 23`
- **iOS:** `iOS 15.4`


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
  }
  // Firebase push (android and ios only):
  androidMain.dependencies {
    api("io.github.mirzemehdi:kmpnotifier-push-firebase:<version>")
  }
  iosMain.dependencies {
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
On application start you initialize the library with the platform configuration and the capabilities (extensions) you want:

```kotlin 
// Local notifications only:
KMPNotifier.initialize(configuration, LocalNotifications)

// Local + Firebase push (android/ios; FirebasePush installs LocalNotifications automatically):
KMPNotifier.initialize(configuration, FirebasePush)
```

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
            ),
            FirebasePush, // omit for local-only usage (pass LocalNotifications instead)
        )
    }
}
```

Also starting from Android 13(API Level 33) you need to ask runtime `POST_NOTIFICATIONS` permission in activity. I created utility function that you can use in activity.
```kotlin
val permissionUtil by permissionUtil()
permissionUtil.askNotificationPermission() //this will ask permission in Android 13(API Level 33) or above, otherwise permission will be granted.
```
  
  
</details>

<details>
  <summary>iOS</summary>

  ### iOS Setup
  For push notifications, include the FirebaseMessaging library in your iOS app from Xcode, call FirebaseApp initialization, and set the apnsToken as below. Don't forget to add Push Notifications and Background Modes (Remote Notifications) signing capability in Xcode. For local-only usage, skip everything Firebase-related and pass `LocalNotifications` instead of `FirebasePush`.

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
            notificationIconPath = composeDesktopResourcesPath() + File.separator + "ic_notification.png"
        ),
        LocalNotifications,
    )
    
    AppInitializer.onApplicationStart()
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
You can send either local or push notification.

### Local Notification
Local notifications are supported on Android, iOS, desktop, JS and wasm targets. Image is supported on Android and iOS 
#### Send notification

```kotlin
KMPNotifier.localNotifier.notify {
  id = Random.nextInt(0, Int.MAX_VALUE)
  title = "Title from KMPNotifier"
  body = "Body message from KMPNotifier"
  payloadData = mapOf(
    Notifier.KEY_URL to "https://github.com/mirzemehdi/KMPNotifier/",
    "extraKey" to "randomValue"
  )
  image = NotificationImage.Url("https://github.com/user-attachments/assets/a0f38159-b31d-4a47-97a7-cc230e15d30b")
}
```

#### Remove notification by Id or all notifications

```kotlin
val notifier = KMPNotifier.localNotifier

notifier.remove(notificationId) //Removes notification by Id  

notifier.removeAll() //Removes all notification

```

#### Notification click and action buttons

Clicks and action buttons are shared events — they work for both local and push notifications:

```kotlin
KMPNotifier.addListener(object : KMPNotifier.Listener {
  override fun onNotificationClicked(data: PayloadData) {
    println("Notification clicked, payload: $data")
  }

  override fun onAction(actionId: String, notificationId: Int, payload: Map<String, Any?>) {
    println("Notification action $actionId triggered")
  }
})
```

### Push Notification
Push notifications are supported only for Android and iOS, using the `kmpnotifier-push-firebase` module.

#### Listen for push notification events
Push-specific events (token updates, push payloads) use `PushListener`:

```kotlin
FirebasePush.addListener(object : PushListener {
  override fun onNewToken(token: String) {
    println("onNewToken: $token") //Update user token in the server if needed
  }

  override fun onPushNotification(title: String?, body: String?) {
    println("Push Notification notification title: $title")
  }

  override fun onPayloadData(data: PayloadData) {
    println("Push Notification payloadData: $data") //PayloadData is just typeAlias for Map<String,*>.
  }

  override fun onPushNotificationWithPayloadData(title: String?, body: String?, data: PayloadData) {
    println("Push Notification is received: Title: $title and Body: $body and payloadData: $data")
  }
}) 
```

And you need to call below platform-specific functions in order to receive payload data properly.
##### Android
Call `KMPNotifier.onCreateOrOnNewIntent(intent)` on launcher Activity's `onCreate` and `onNewIntent` methods.
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
Call `FirebasePush.onApplicationDidReceiveRemoteNotification(userInfo: userInfo)` on application's `didReceiveRemoteNotification` method.

```swift
 func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any]) async -> UIBackgroundFetchResult {
      FirebasePush.shared.onApplicationDidReceiveRemoteNotification(userInfo: userInfo)
      return UIBackgroundFetchResult.newData
 }

```  


#### Other functions
```kotlin
KMPNotifier.firebasePushNotifier.getToken() //Get current user push notification token
KMPNotifier.firebasePushNotifier.deleteMyToken() //Delete user's token for example when user logs out 
KMPNotifier.firebasePushNotifier.subscribeToTopic("new_users") 
KMPNotifier.firebasePushNotifier.unSubscribeFromTopic("new_users") 
```
For setting custom notification sound, check https://github.com/mirzemehdi/KMPNotifier/pull/61#issuecomment-2275850021  
For setting Intent data in Android (for deeplink), check https://github.com/mirzemehdi/KMPNotifier/pull/60#issue-2454489089    
For permissionUtil, or manually asking notification permission check https://github.com/mirzemehdi/KMPNotifier/pull/27#issuecomment-2083639907  

### Logging

If you want to see internal logs of the library, you can set a logger using:

```kotlin
KMPNotifier.setLogger { message ->
    // Log the message
    println(message)
}
```

## Migrating from 1.x

The old `NotifierManager` API (from the `kmpnotifier` artifact) keeps working in 2.x —
it is deprecated and forwards to the new API. See [MIGRATION.md](MIGRATION.md).
