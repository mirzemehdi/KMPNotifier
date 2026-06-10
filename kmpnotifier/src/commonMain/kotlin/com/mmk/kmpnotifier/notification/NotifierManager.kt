package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.logger.Logger
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil


/**
 * Deprecated 1.x entry point of KMPNotifier.
 *
 * It keeps working and forwards to the new modular API. Migrate to [com.mmk.kmpnotifier.KMPNotifier]
 * with the `LocalNotifications` (kmpnotifier-local) and `FirebasePush` (kmpnotifier-push-firebase)
 * extensions — see MIGRATION.md. Removal is planned for 3.0.0.
 */
@Deprecated(
    message = "NotifierManager is replaced by KMPNotifier with pluggable extensions " +
            "(LocalNotifications, FirebasePush). See MIGRATION.md. Removal planned for 3.0.0.",
    replaceWith = ReplaceWith("KMPNotifier", "com.mmk.kmpnotifier.KMPNotifier"),
)
public object NotifierManager {

    /**
     * Call initialize function on Application Start.
     * @param configuration pass either ios or android configuration depending on platform
     * @see NotificationPlatformConfiguration.Ios
     * @see NotificationPlatformConfiguration.Android
     */
    @Deprecated(
        message = "Use KMPNotifier.initialize(configuration, FirebasePush) for push (android/ios) " +
                "or KMPNotifier.initialize(configuration, LocalNotifications) for local-only usage. " +
                "See MIGRATION.md.",
    )
    public fun initialize(configuration: NotificationPlatformConfiguration) {
        NotifierManagerImpl.initialize(configuration)
    }

    /**
     * Creates local Notifier instance
     */
    @Deprecated(
        message = "Use KMPNotifier.localNotifier (or LocalNotifications.notifier). See MIGRATION.md.",
        replaceWith = ReplaceWith(
            "KMPNotifier.localNotifier",
            "com.mmk.kmpnotifier.KMPNotifier",
            "com.mmk.kmpnotifier.local.localNotifier",
        ),
    )
    public fun getLocalNotifier(): Notifier {
        return NotifierManagerImpl.getLocalNotifier()
    }

    /**
     * Creates push Notifier instance (Firebase Push Notification)
     */
    @Deprecated(
        message = "Use KMPNotifier.firebasePushNotifier (or FirebasePush.notifier) from kmpnotifier-push-firebase " +
                "on android/ios. See MIGRATION.md.",
    )
    public fun getPushNotifier(): PushNotifier {
        return NotifierManagerImpl.getPushNotifier()
    }


    /**
     * For listening updates such as push notification token changes. This will add new listener to listener list.
     */
    @Deprecated(
        message = "Shared events (clicks, actions) moved to KMPNotifier.addListener(KMPNotifier.Listener); " +
                "push events moved to FirebasePush.addListener(PushListener). See MIGRATION.md.",
    )
    public fun addListener(listener: Listener) {
        NotifierManagerImpl.addListener(listener)
    }

    /**
     * For listening updates such as push notification token changes. This will set one listener only.
     * You can set null to remove listener.
     */
    @Deprecated(
        message = "Shared events (clicks, actions) moved to KMPNotifier.setListener(KMPNotifier.Listener); " +
                "push events moved to FirebasePush.setListener(PushListener). See MIGRATION.md.",
    )
    public fun setListener(listener: Listener?) {
        NotifierManagerImpl.setListener(listener)
    }

    /**
     *
     * Returns permission util that can be used to check and ask notification permission
     * However in Android you need to use in Activity like below:
     *
     * val permissionUtil by permissionUtil()
     * permissionUtil.askNotificationPermission() //this will ask permission in Android 13(API Level 33) or above, otherwise permission will be granted.
     *
     * @return PermissionUtil class instance
     */
    @Deprecated(
        message = "Use KMPNotifier.permissionUtil. See MIGRATION.md.",
        replaceWith = ReplaceWith("KMPNotifier.permissionUtil", "com.mmk.kmpnotifier.KMPNotifier"),
    )
    public fun getPermissionUtil(): PermissionUtil {
        return NotifierManagerImpl.getPermissionUtil()
    }


    /**
     * Deprecated 1.x listener carrying both shared and push events.
     *
     * Replaced by [com.mmk.kmpnotifier.KMPNotifier.Listener] (clicks, actions — fired for both
     * local and push notifications) and `PushListener` in kmpnotifier-push-firebase
     * (token updates, push payloads).
     */
    @Deprecated(
        message = "Split into KMPNotifier.Listener (shared events: clicks, actions) and " +
                "PushListener in kmpnotifier-push-firebase (push events). See MIGRATION.md.",
    )
    public interface Listener {
        /**
         * Called when push notification token is updated, or initialized first time
         * @param token Push Notification token
         */
        public fun onNewToken(token: String) {}

        /**
         * Called when "Push Notification" data type message is available
         * @see onPushNotificationWithPayloadData For Receiving one callback instead of two
         * @see onPushNotification for receiving "Push Notification" notification type message.
         * @param data Push Notification Payload Data
         */
        public fun onPayloadData(data: PayloadData) {}

        /**
         * Called when "Push Notification" notification type message is received.
         * @see onPushNotificationWithPayloadData For Receiving one callback instead of two
         * @see onPayloadData for receiving "Push Notification" data type message.
         * @param title Notification title
         * @param body Notification body message
         */
        public fun onPushNotification(title: String?, body: String?) {}

        /**
         * Called when "Push Notification"  is received.
         * @param title Notification title
         * @param body Notification body message
         * @param data Notification Payload data
         */
        public fun onPushNotificationWithPayloadData(title: String?, body: String?, data: PayloadData) {}

        /**
         * Called when notification is clicked
         * @param data Push Notification Payload Data
         */
        public fun onNotificationClicked(data: PayloadData) {}


        /**
         * Called when a notification action is triggered
         * @param actionId The ID of the action that was triggered
         * @param notificationId The ID of the notification
         * @param payload The payload data from the notification
         */
        public fun onAction(actionId: String, notificationId: Int, payload: Map<String, Any?>){}
    }

    /**
     * Sets a custom logger for the library to use.
     * By default, the library uses an empty logger that doesn't log anything.
     *
     * @param logger The logger to use
     */
    @Deprecated(
        message = "Use KMPNotifier.setLogger. See MIGRATION.md.",
        replaceWith = ReplaceWith("KMPNotifier.setLogger(logger)", "com.mmk.kmpnotifier.KMPNotifier"),
    )
    public fun setLogger(logger: Logger) {
        NotifierManagerImpl.setLogger(logger)
    }
}
