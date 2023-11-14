# KMPNotifier - Kotlin Multiplatform Push Notification
[![Build](https://github.com/mirzemehdi/KMPNotifier/actions/workflows/build.yml/badge.svg)](https://github.com/mirzemehdi/KMPNotifier/actions/workflows/build.yml) 
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mirzemehdi/kmpnotifier?color=blue)](https://search.maven.org/search?q=g:io.github.mirzemehdi)

![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat)

Simple and easy to use Kotlin Multiplatform Push Notification library (using Firebase Cloud Messaging) targeting ios and android.   
You can check out [Documentation](https://mirzemehdi.github.io/KMPNotifier) for full library api information.

## Features
  - 🔔 Local and Push Notification (Firebase Cloud Messaging)
  - 📱 Multiplatform (android and iOS)

## Installation
Before starting you need to setup basic setup using Firebase official guideline (like initializing project in Firebase, adding `google-services.json` to android, `GoogleService-Info.plist` to iOS).

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

Then in your shared module you add dependency in `commonMain`. Latest version: [![Maven Central](https://img.shields.io/maven-central/v/io.github.mirzemehdi/kmpnotifier?color=blue)](https://search.maven.org/search?q=g:io.github.mirzemehdi)
```kotlin
sourceSets {
  commonMain.dependencies {
    implementation("io.github.mirzemehdi:kmpnotifier:<version>") 
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
        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.ic_launcher_foreground,
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
	In progress ....
   ios
setup
https://firebase.google.com/docs/ios/setup
https://firebase.google.com/docs/cloud-messaging/ios/client

for easy setup
FirebaseAppDelegateProxyEnabled YES in info.plist



add FIrebaseMessaging and FirebaseCore to your project (for some reason otherwise it has some errors)

on Application start call initialize
FirebaseApp.configure()
and initialize


class AppDelegate: NSObject, UIApplicationDelegate {

func application(_ application: UIApplication,
didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {

      FirebaseApp.configure()
      AppInitializer.shared.initialize(
          isDebug: true, onKoinStart: { _ in })
    return true
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
 
</details>



