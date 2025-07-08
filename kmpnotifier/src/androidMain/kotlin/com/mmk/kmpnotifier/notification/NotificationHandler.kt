package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.Constants
import com.mmk.kmpnotifier.extensions.shouldShowNotification


internal class NotificationHandler(
    private val notifierManager: NotifierManagerImpl,
    private val notifier: Notifier
) {
     fun handle(title: String?, body: String?, data: Map<String, String>) {
        title?.let {
            if (notifierManager.shouldShowNotification()) {
                notifier.notify(
                    title = title,
                    body = body.orEmpty(),
                    payloadData = data
                )
            }
            notifierManager.onPushNotification(title = title, body = body)
        }

        val payloadDataWithClickAction = if (data.isNotEmpty())
            data + (Constants.ACTION_NOTIFICATION_CLICK to Constants.ACTION_NOTIFICATION_CLICK)
        else
            data

        if (payloadDataWithClickAction.isNotEmpty())
            notifierManager.onPushPayloadData(data = payloadDataWithClickAction)

        notifierManager.onPushNotificationWithPayloadData(
            title = title,
            body = body,
            data = payloadDataWithClickAction
        )
    }
}
