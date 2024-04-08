# KMPNotifier - Kotlin Multiplatform Push Notification
[![Build](https://github.com/mirzemehdi/KMPNotifier/actions/workflows/build.yml/badge.svg)](https://github.com/mirzemehdi/KMPNotifier/actions/workflows/build.yml) 
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mirzemehdi/kmpnotifier?color=blue)](https://search.maven.org/search?q=g:io.github.mirzemehdi)

![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat)

Simple and easy to use Kotlin Multiplatform Push Notification library (using Firebase Cloud Messaging) targeting ios and android.  
This library is used in [FindTravelNow](https://github.com/mirzemehdi/FindTravelNow-KMM/) production KMP project.
You can check out [Documentation](https://mirzemehdi.github.io/KMPNotifier) for full library api information.

## Features
  - 🔔 Local and Push Notification (Firebase Cloud Messaging)
  - 📱 Multiplatform (android and iOS)

## Installation
Before starting you need to setup basic setup using Firebase official guideline (like initializing project in Firebase, adding `google-services.json` to android, `GoogleService-Info.plist` to iOS).

## Minimum Requirements

- **Android:** `minSdkVersion 21`
- **iOS:** `iOS 14.1`


### Gradle Setup
KMPNotifier is available on Maven Central. In your root project `build.gradle.kts` file (or `settings.gradle` file) add `mavenCentral()` to repositories, and  add `google-services` plugin to plugins.

```kotlin
plugins {
  id("com.android.application") version "8.1.3" apply false
  id("org.jetbrains.kotlin.multiplatform") version "1.9.20" apply false
  id("com.google.gms.google-services") version "4.4.0" apply false
}

repositories { 
  mavenCentral()
}
```

Then in your shared module you add dependency in `commonMain`. Latest version: [![Maven Central](https://img.shields.io/maven-central/v/io.github.mirzemehdi/kmpnotifier?color=blue)](https://search.maven.org/search?q=g:io.github.mirzemehdi). In iOS framework part export this library as well.
```kotlin
sourceSets {
  commonMain.dependencies {
    api("io.github.mirzemehdi:kmpnotifier:<version>") // in iOS export this library
  }
}
```

And in androidApp `build.gradle.kts` file you apply `google-services` plugin  
```kotlin
plugins {
  id("com.android.application")
  id("com.google.gms.google-services")
}
```


### Platform Setup
In both platforms on Application Start you need to initialize library using 
```kotlin 
NotifierManager.initialize(NotificationPlatformConfiguration) //passing android or ios configuration depending on the platform
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
         * When set showPushNotification to false foreground push  notification will not be shown to user.
         * You can still get notification content using #onPushNotification listener method.
         */
        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.ic_launcher_foreground,
                showPushNotification = true,
            )
        )
    }
}
```

Also starting from Android 13(API Level 33) you need to ask runtime `POST_NOTIFICATIONS` in activity. I created utility function that you can use in activity.
```kotlin
val permissionUtil by permissionUtil()
permissionUtil.askNotificationPermission() //this will ask permission in Android 13(API Level 33) or above, otherwise permission will be granted.
```
  
  
</details>

<details>
  <summary>iOS</summary>

  ### iOS Setup
  First you just need to include FirebaseMessaging library to your ios app from Xcode. Then on application start you need to call both FirebaseApp initialization and NotifierManager initialization methods, and apnsToken setting as below. Don't forget to add Push Notifications and Background Modes (Remote Notifications) signing capability in Xcode.

```swift
import SwiftUI
import shared
import FirebaseCore
import FirebaseMessaging

class AppDelegate: NSObject, UIApplicationDelegate {

  func application(_ application: UIApplication,
                   didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {

      FirebaseApp.configure() //important
      
      //By default showPushNotification value is true.
      //When set showPushNotification to false foreground push  notification will not be shown.
      //You can still get notification content using #onPushNotification listener method.
      NotifierManager.shared.initialize(configuration: NotificationPlatformConfigurationIos(showPushNotification: true))
      
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

## Usage
You can send either local or push notification.

### Local Notification

#### Send notification

```kotlin
val notifier = NotifierManager.getLocalNotifier()
val notificationId = notifier.notify("Title", "Body") 
// or you can use below to specify ID yourself
notifier.notify(1, "Title", "Body")


```

#### Remove notification by Id or all notifications

```kotlin
notifer.remove(notificationId) //Removes notification by Id  

notifier.removeAll() //Removes all notification

```

### Push Notification

#### Listen for push notification token changes
In this method you can send notification token to the server.

```kotlin
NotifierManager.addListener(object : NotifierManager.Listener {
  override fun onNewToken(token: String) {
    println("onNewToken: $token") //Update user token in the server if needed
  }
}) 
```

#### Receive notification type messages  
```kotlin
NotifierManager.addListener(object : NotifierManager.Listener {
  override fun onPushNotification(title:String?,body:String?) {
    println("Push Notification notification title: $title")
  }
}) 
```


#### Receive data payload
```kotlin
NotifierManager.addListener(object : NotifierManager.Listener {
  override fun onPayloadData(data: PayloadData) {
    println("Push Notification payloadData: $data") //PayloadData is just typeAlias for Map<String,*>.
  }
}) 
```
And you need to call below platform-specific functions in order to receive payload data properly.
##### Android
Call `NotifierManager.onCreateOrOnNewIntent(intent)` on launcher Activity's `onCreate` and `onNewIntent` methods.
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
   super.onCreate(savedInstanceState)
      NotifierManager.onCreateOrOnNewIntent(intent)
      ...
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        NotifierManager.onCreateOrOnNewIntent(intent)
    }

```

##### iOS
Call `NotifierManager.onApplicationDidReceiveRemoteNotification(userInfo: userInfo)` on application's `didReceiveRemoteNotification` method.

```
 func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any]) async -> UIBackgroundFetchResult {
      NotifierManager.shared.onApplicationDidReceiveRemoteNotification(userInfo: userInfo)
      return UIBackgroundFetchResult.newData
 }

```  


#### Detecting notification click and get payload data
Make sure you follow previous step for getting payload data properly.
```kotlin
NotifierManager.addListener(object : NotifierManager.Listener {
    override fun onNotificationClicked(data: PayloadData) {
        super.onNotificationClicked(data)
        println("Notification clicked, Notification payloadData: $data")
    }
}) 
```   


#### Other functions
```kotlin
NotifierManager.getPushNotifier().getToken() //Get current user push notification token
NotifierManager.getPushNotifier().deleteMyToken() //Delete user's token for example when user logs out 
NotifierManager.getPushNotifier().subscribeToTopic("new_users") 
NotifierManager.getPushNotifier().unSubscribeFromTopic("new_users") 
```




