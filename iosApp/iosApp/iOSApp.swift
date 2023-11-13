import SwiftUI
import sample

@main
struct iOSApp: App {
    
    init() {
            NotifierManager.shared.initialize(configuration: NotificationPlatformConfigurationIos.shared)
    }
    
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
