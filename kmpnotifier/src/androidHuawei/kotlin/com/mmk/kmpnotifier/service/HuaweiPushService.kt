package com.mmk.kmpnotifier.service

import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import com.mmk.kmpnotifier.logger.currentLogger
import com.mmk.kmpnotifier.notification.NotificationHandler
import com.mmk.kmpnotifier.notification.NotifierManagerImpl


internal class HuaweiPushService : HmsMessageService() {
    private val notifierManager by lazy { NotifierManagerImpl }
    private val notifier by lazy { notifierManager.getLocalNotifier() }
    private val handler by lazy { NotificationHandler(notifierManager, notifier) }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        currentLogger.log("HuaweiPushService: onNewToken is called")
        token?.let { notifierManager.onNewToken(it) }
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        handler.handle(
            title = message?.notification?.title,
            body = message?.notification?.body,
            data = message?.dataOfMap.orEmpty()
        )
    }
}
