package com.mmk.kmpnotifier.testutil

import com.mmk.kmpnotifier.notification.NotifierManagerImpl
import com.mmk.kmpnotifier.notification.PayloadData

/**
 * Single indirection point for dispatching internal notifier events from tests.
 * Tests go through this object instead of calling internals directly, so that
 * a refactor of the internal event hub only requires rewiring this one file.
 */
internal object TestDispatch {

    fun newToken(token: String): Unit = NotifierManagerImpl.onNewToken(token)

    fun pushPayloadData(data: PayloadData): Unit = NotifierManagerImpl.onPushPayloadData(data)

    fun pushNotification(title: String?, body: String?): Unit =
        NotifierManagerImpl.onPushNotification(title = title, body = body)

    fun pushNotificationWithPayloadData(
        title: String? = null,
        body: String? = null,
        data: PayloadData,
    ): Unit = NotifierManagerImpl.onPushNotificationWithPayloadData(
        title = title,
        body = body,
        data = data,
    )

    fun notificationClicked(data: PayloadData): Unit =
        NotifierManagerImpl.onNotificationClicked(data)

    fun action(actionId: String, notificationId: Int, payload: Map<String, Any?>): Unit =
        NotifierManagerImpl.onAction(
            actionId = actionId,
            notificationId = notificationId,
            payload = payload,
        )
}
