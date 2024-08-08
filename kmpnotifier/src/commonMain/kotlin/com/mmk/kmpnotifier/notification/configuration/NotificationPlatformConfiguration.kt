package com.mmk.kmpnotifier.notification.configuration


/**
 * You can configure some customization for notifications depending on the platform
 */
public sealed interface NotificationPlatformConfiguration {

    /**
     * Android Notification Customization. Create this object in android source.
     *
     * @param  notificationIconResId icon ResourceId (R.drawable.ic_notification)
     * @param notificationIconColorResId optional icon color ResourceId (R.color.yellow)
     * @param notificationChannelData optional notification channel data for General or Miscellaneous notifications
     * @see NotificationChannelData
     * @param showPushNotification Default value is true, by default when push notification is
     * received it will be shown to user. When set to false, it will not be shown to user,
     * but you can still get notification content using
     * @see com.mmk.kmpnotifier.notification.NotifierManager.Listener.onPushNotification
     */
    public class Android(
        public val notificationIconResId: Int,
        public val notificationIconColorResId: Int? = null,
        public val notificationChannelData: NotificationChannelData = NotificationChannelData(),
        public val showPushNotification: Boolean = true,
    ) : NotificationPlatformConfiguration {

        /**
         * By default Notification channel with below configuration is created but you can change it
         * @param id for General(or Miscellaneous or Other) notifications. Default value: "DEFAULT_NOTIFICATION_CHANNEL_ID"
         * @param name this is the title that is shown on app notification channels. Default value is "General"
         * Usually it is either General or Miscellaneous or Miscellaneous in most apps
         * @param description Notification description
         * @param soundUri  Custom Notification sound, uri should be converted to String.
         */
        public class NotificationChannelData(
            public val id: String = "DEFAULT_NOTIFICATION_CHANNEL_ID",
            public val name: String = "General",
            public val description: String = "",
            public val soundUri: String? = null
        )

    }

    /**
     * Ios notification customization.
     *
     * @param showPushNotification Default value is true,
     * by default when push notification is received it will be shown to user.
     * When set to false, it will not be shown to user, but you can still get notification content using
     * @see com.mmk.kmpnotifier.notification.NotifierManager.Listener.onPushNotification
     *
     * @param askNotificationPermissionOnStart Default value is true, when library is initialized it
     * will ask notification permission automatically from the user.
     * By setting askNotificationPermissionOnStart false, you can customize to ask permission whenever you want.
     *
     * @param notificationSoundName Custom notification sound name. Default value is null.
     * If set null, default notification sound will be played.
     * Place the file in the Resources directory of your iOS target.
     * Ensure the file is added to your target's "Build Phases" under "Copy Bundle Resources".
     */
    public data class Ios(
        public val showPushNotification: Boolean = true,
        public val askNotificationPermissionOnStart: Boolean = true,
        public val notificationSoundName: String? = null
    ) : NotificationPlatformConfiguration


    /**
     * Desktop notification customization.
     *
     * @param showPushNotification Default value is true,
     * by default when push notification is received it will be shown to user.
     * When set to false, it will not be shown to user, but you can still get notification content using
     * @see com.mmk.kmpnotifier.notification.NotifierManager.Listener.onPushNotification
     *
     * @param  notificationIconPath Notification icon path

     */
    public data class Desktop(
        public val showPushNotification: Boolean = true,
        public val notificationIconPath: String? = null
    ) : NotificationPlatformConfiguration

    /**
     * Web notification customization.
     *
     * @param askNotificationPermissionOnStart Default value is true, when library is initialized it
     * will ask notification permission automatically from the user.
     * By setting askNotificationPermissionOnStart false, you can customize to ask permission whenever you want.
     *
     *@param  notificationIconPath Notification icon path
     *
     */
    public class Web(
        public val askNotificationPermissionOnStart: Boolean = true,
        public val notificationIconPath: String? = null
    ) : NotificationPlatformConfiguration
}