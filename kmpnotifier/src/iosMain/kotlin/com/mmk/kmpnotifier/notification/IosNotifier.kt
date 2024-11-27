package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.extensions.onNotificationClicked
import com.mmk.kmpnotifier.extensions.onUserNotification
import com.mmk.kmpnotifier.extensions.shouldShowNotification
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.IosPermissionUtil
import com.prinum.utils.logger.Logger
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
import kotlin.random.Random

internal class IosNotifier(
    private val permissionUtil: IosPermissionUtil,
    private val notificationCenter: UNUserNotificationCenter,
    private val iosNotificationConfiguration: NotificationPlatformConfiguration.Ios
) : Notifier {

    companion object{
        private const val TAG = "IosNotifier"
    }
    override fun notify(title: String, body: String, payloadData: Map<String, String>): Int {
        val notificationID = Random.nextInt(0, Int.MAX_VALUE)
        notify(notificationID, title, body, payloadData)
        return notificationID
    }

    override fun notify(id: Int, title: String, body: String, payloadData: Map<String, String>) {
        permissionUtil.askNotificationPermission {
            val notificationContent = UNMutableNotificationContent().apply {
                setTitle(title)
                setBody(body)
                setSound()
                setUserInfo(userInfo + payloadData)
            }
            val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)
            Logger.d(TAG, "Notify iOS message with title: $title")
            val notificationRequest = UNNotificationRequest.requestWithIdentifier(
                identifier = id.toString(),
                content = notificationContent,
                trigger = trigger
            )

            notificationCenter.addNotificationRequest(notificationRequest) { error ->
                error?.let { Logger.d(TAG, "Error showing notification: $error") }
            }
        }
    }

    private fun UNMutableNotificationContent.setSound() {
        val customSoundPath = iosNotificationConfiguration.notificationSoundName
        val customNotificationSound = customSoundPath?.let {
            UNNotificationSound.soundNamed(customSoundPath)
        }
        val notificationSound = customNotificationSound ?: UNNotificationSound.defaultSound
        setSound(notificationSound)
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
            Logger.d("IosNotifier", "didReceiveNotificationResponse. Content: $notificationContent")
            NotifierManager.onUserNotification(notificationContent)
            NotifierManager.onNotificationClicked(notificationContent)
            if (NotifierManager.shouldShowNotification(notificationContent)) withCompletionHandler()
        }

        override fun userNotificationCenter(
            center: UNUserNotificationCenter,
            willPresentNotification: UNNotification,
            withCompletionHandler: (UNNotificationPresentationOptions) -> Unit,
        ) {
            val notificationContent = willPresentNotification.request.content
            Logger.d("IosNotifier", "willPresentNotification. Content: $notificationContent")
            NotifierManager.onUserNotification(notificationContent)
            if (NotifierManager.shouldShowNotification(notificationContent)) withCompletionHandler(
                IosPermissionUtil.NOTIFICATION_PERMISSIONS
            )
        }
    }
}

