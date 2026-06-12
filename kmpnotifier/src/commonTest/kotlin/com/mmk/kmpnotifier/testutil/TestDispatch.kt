@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.testutil

import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierEventHub
import com.mmk.kmpnotifier.notification.PayloadData

/**
 * Single indirection point for dispatching internal notifier events from tests.
 * Events are emitted into the core event hub, exactly like the platform event sources do.
 */
internal object TestDispatch {

    fun newToken(token: String): Unit = NotifierEventHub.emitNewToken(token)

    fun pushPayloadData(data: PayloadData): Unit = NotifierEventHub.emitPushPayloadData(data)

    fun pushNotification(title: String?, body: String?): Unit =
        NotifierEventHub.emitPushNotification(title = title, body = body)

    fun pushNotificationWithPayloadData(
        title: String? = null,
        body: String? = null,
        data: PayloadData,
    ): Unit = NotifierEventHub.emitPushNotificationWithPayloadData(
        title = title,
        body = body,
        data = data,
    )

    fun notificationClicked(data: PayloadData): Unit =
        NotifierEventHub.emitNotificationClicked(data)

    fun action(actionId: String, notificationId: Int, payload: Map<String, Any?>): Unit =
        NotifierEventHub.emitAction(
            actionId = actionId,
            notificationId = notificationId,
            payload = payload,
        )
}
