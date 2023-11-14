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
     */
    public class Android(
        public val notificationIconResId: Int,
        public val notificationIconColorResId: Int? = null,
        public val notificationChannelData: NotificationChannelData = NotificationChannelData(),
    ) : NotificationPlatformConfiguration {

        /**
         * By default Notification channel with below configuration is created but you can change it
         * @param id for General(or Miscellaneous or Other) notifications. Default value: "DEFAULT_NOTIFICATION_CHANNEL_ID"
         * @param name this is the title that is shown on app notification channels. Default value is "General"
         * Usually it is either General or Miscellaneous or Miscellaneous in most apps
         * @param description Notification description
         */
        public class NotificationChannelData(
            public val id: String = "DEFAULT_NOTIFICATION_CHANNEL_ID",
            public val name: String = "General",
            public val description: String = "",
        )

    }

    /**
     * Ios notification customization. No customization yet :(
     */
   public data object Ios : NotificationPlatformConfiguration
}