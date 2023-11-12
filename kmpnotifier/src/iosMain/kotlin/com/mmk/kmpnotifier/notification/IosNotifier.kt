package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.permission.IosPermissionUtil
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationResponse
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject

internal class IosNotifier(
    private val permissionUtil: IosPermissionUtil,
    private val notificationCenter: UNUserNotificationCenter,
) : Notifier {
    override fun notify(title: String, body: String) {
        permissionUtil.askNotificationPermission {
            val notificationContent = UNMutableNotificationContent().apply {
                setTitle(title)
                setBody(body)
                setSound(UNNotificationSound.defaultSound)
            }
            val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)
            val notificationRequest = UNNotificationRequest.requestWithIdentifier(
                identifier = "general-notification-id",
                content = notificationContent,
                trigger = trigger
            )

            notificationCenter.addNotificationRequest(notificationRequest) { error ->
                println("Error showing notification: $error")
            }
        }

        //For showing notification in foreground too
        UNUserNotificationCenter.currentNotificationCenter().delegate = NotificationDelegate()
    }

    private class NotificationDelegate : UNUserNotificationCenterDelegateProtocol, NSObject() {
        override fun userNotificationCenter(
            center: UNUserNotificationCenter,
            didReceiveNotificationResponse: UNNotificationResponse,
            withCompletionHandler: () -> Unit,
        ) {
            withCompletionHandler()
        }

        override fun userNotificationCenter(
            center: UNUserNotificationCenter,
            willPresentNotification: UNNotification,
            withCompletionHandler: (UNNotificationPresentationOptions) -> Unit,
        ) {
            withCompletionHandler(IosPermissionUtil.NOTIFICATION_PERMISSIONS)
        }
    }
}

