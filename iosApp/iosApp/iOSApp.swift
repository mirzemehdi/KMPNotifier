import SwiftUI
import sample

@main
struct iOSApp: App {
    init() {
        NotifierFactory.shared.initialize(configuration: NotificationPlatformConfigurationIos.shared)
    }

    var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}