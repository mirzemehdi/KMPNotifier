package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.extensions.onApplicationDidReceiveRemoteNotification
import com.mmk.kmpnotifier.extensions.onUserNotification
import com.mmk.kmpnotifier.extensions.shouldShowNotification
import com.mmk.kmpnotifier.permission.IosPermissionUtil
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationContent
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationResponse
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject
import kotlin.random.Random

internal class IosNotifier(
    private val permissionUtil: IosPermissionUtil,
    private val notificationCenter: UNUserNotificationCenter,
) : Notifier {


    override fun notify(title: String, body: String): Int {
        val notificationID = Random.nextInt(0, Int.MAX_VALUE)
        notify(notificationID, title, body)
        return notificationID
    }

    override fun notify(id: Int, title: String, body: String) {
        permissionUtil.askNotificationPermission {
            val notificationContent = UNMutableNotificationContent().apply {
                setTitle(title)
                setBody(body)
                setSound(UNNotificationSound.defaultSound)
            }
            val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)
            val notificationRequest = UNNotificationRequest.requestWithIdentifier(
                identifier = id.toString(),
                content = notificationContent,
                trigger = trigger
            )

            notificationCenter.addNotificationRequest(notificationRequest) { error ->
                error?.let { println("Error showing notification: $error") }
            }
        }
    }

    override fun remove(id: Int) {
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(listOf(id.toString()))
    }

    override fun removeAll() {
        notificationCenter.removeAllDeliveredNotifications()
    }

    internal class NotificationDelegate : UNUserNotificationCenterDelegateProtocol, NSObject() {
        override fun userNotificationCenter(
            center: UNUserNotificationCenter,
            didReceiveNotificationResponse: UNNotificationResponse,
            withCompletionHandler: () -> Unit,
        ) {
            val notificationContent = didReceiveNotificationResponse.notification.request.content
            val actionIdentifier = didReceiveNotificationResponse.actionIdentifier
            println("ActionIdentifier: $actionIdentifier")
            NotifierManager.onUserNotification(notificationContent)
            if (NotifierManager.shouldShowNotification(notificationContent)) withCompletionHandler()
        }

        override fun userNotificationCenter(
            center: UNUserNotificationCenter,
            willPresentNotification: UNNotification,
            withCompletionHandler: (UNNotificationPresentationOptions) -> Unit,
        ) {
            val notificationContent = willPresentNotification.request.content
            NotifierManager.onUserNotification(notificationContent)
            if (NotifierManager.shouldShowNotification(notificationContent)) withCompletionHandler(IosPermissionUtil.NOTIFICATION_PERMISSIONS)
        }
    }
}

