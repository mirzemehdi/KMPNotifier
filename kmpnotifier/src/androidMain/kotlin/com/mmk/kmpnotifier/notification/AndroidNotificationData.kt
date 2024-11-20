package com.mmk.kmpnotifier.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

public class AndroidNotificationData private constructor(
    public val context: Context,
    public val notificationManager: NotificationManager
) {
    public var notificationChannelData: NotificationPlatformConfiguration.Android.NotificationChannelData? = null
        private set
    public var notificationId: Int = -1
        private set
    public var notificationIconResId: Int = -1
        private set
    public var notificationIconColorResId: Int? = null
        private set
    public var pendingIntent: PendingIntent? = null
        private set
    public var payloadData: Map<String, String>? = null
        private set

    internal companion object {

        /**
         * Builder that assembles data required for the Android notification
         * @param context Android application context. By default
         * using androidx-startup Context reference is passed without needing to pass one manually.
         * @param notificationManager System notification manager that could post notifications
         */
        class Builder(context: Context, notificationManager: NotificationManager) {
            private val builder: AndroidNotificationData = AndroidNotificationData(
                context = context,
                notificationManager = notificationManager
            )

            /**
             * Set notification id
             * @param notificationId ID of the notification
             */
            fun setNotificationId(notificationId: Int): Builder {
                builder.notificationId = notificationId
                return this
            }

            /**
             * Set channel data
             * @param channelData notification channel data for General or Miscellaneous notifications
             */
            fun setChannelData(
                channelData: NotificationPlatformConfiguration.Android.NotificationChannelData
            ): Builder {
                builder.notificationChannelData = channelData
                return this
            }

            /**
             * @param notificationIconResId icon ResourceId (R.drawable.ic_notification)
             */
            fun setNotificationIconRes(notificationIconResId: Int): Builder {
                builder.notificationIconResId = notificationIconResId
                return this
            }

            /**
             * @param notificationIconColorResId icon color ResourceId (R.color.yellow)
             */
            fun setNotificationIconColorResId(notificationIconColorResId: Int?): Builder {
                builder.notificationIconColorResId = notificationIconColorResId
                return this
            }

            /**
             * @param pendingIntent action that invokes on notification click
             */
            fun setPendingIntent(pendingIntent: PendingIntent?): Builder {
                builder.pendingIntent = pendingIntent
                return this
            }

            /**
             * @param payloadData extra data for the push notification
             */
            fun setPayloadData(payloadData: Map<String, String>): Builder {
                builder.payloadData = payloadData
                return this
            }

            /**
             * @return notification data required for its displaying
             */
            fun build(): AndroidNotificationData {
                return builder
            }
        }
    }
}