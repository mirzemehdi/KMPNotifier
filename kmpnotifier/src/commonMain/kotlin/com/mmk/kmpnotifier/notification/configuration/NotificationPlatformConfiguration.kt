package com.mmk.kmpnotifier.notification.configuration


public sealed interface NotificationPlatformConfiguration {
    public class Android(
        public val notificationIconResId: Int,
        public val notificationIconColorResId: Int? = null,
        public val notificationChannelData: NotificationChannelData = NotificationChannelData(),
    ) : NotificationPlatformConfiguration {

        public class NotificationChannelData(
            public val id: String = "DEFAULT_NOTIFICATION_CHANNEL_ID",
            public val name: String = "General",
            public val description: String = "",
        )

    }

   public data object Ios : NotificationPlatformConfiguration
}