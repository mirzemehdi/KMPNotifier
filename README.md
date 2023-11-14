# KMPNotifier
Kotlin Multiplatform Notification library targeting ios and android.


Setup

in Both platform Application Start

NotifierManager.initialize(
configuration = NotificationPlatformConfiguration.Android(
R.drawable.ic_launcher_foreground,
)
)

NotifierManager.initialize(
configuration = NotificationPlatformConfiguration.Ios
)

Android

Add google-services.json

Root-level (project-level) Gradle file (<project>/build.gradle.kts):
plugins {

// ...


// Add the dependency for the Google services Gradle plugin

id("com.google.gms.google-services") version "4.4.0" apply false

}

Then, in your module (app-level) build.gradle.kts file, add both the google-services plug-in and any Firebase SDKs that you want to use in your app:
Module (app-level) Gradle file (<project>/<app-module>/build.gradle.kts): 


plugins {

id("com.android.application")

// Add the Google services Gradle plugin

id("com.google.gms.google-services")

...

}


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