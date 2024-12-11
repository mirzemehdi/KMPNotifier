package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.extensions.onNotificationClicked
import com.mmk.kmpnotifier.extensions.onUserNotification
import com.mmk.kmpnotifier.extensions.shouldShowNotification
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.IosPermissionUtil
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.writeToURL
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationAttachment
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationResponse
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

internal class IosNotifier(
    private val permissionUtil: IosPermissionUtil,
    private val notificationCenter: UNUserNotificationCenter,
    private val iosNotificationConfiguration: NotificationPlatformConfiguration.Ios
) : Notifier {

    private val scope by lazy { MainScope() }


    override fun notify(title: String, body: String, payloadData: Map<String, String>): Int {
        val notificationID = Random.nextInt(0, Int.MAX_VALUE)
        notify {
            this.id = notificationID
            this.title = title
            this.body = body
            this.payloadData = payloadData
        }
        return notificationID
    }

    override fun notify(id: Int, title: String, body: String, payloadData: Map<String, String>) {
        notify {
            this.id = id
            this.title = title
            this.body = body
            this.payloadData = payloadData
        }
    }

    override fun notify(block: NotifierBuilder.() -> Unit) {
        val builder = NotifierBuilder().apply(block)
        permissionUtil.askNotificationPermission {
            scope.launch {
                val notificationContent = UNMutableNotificationContent().apply {
                    setTitle(builder.title)
                    setBody(builder.body)
                    setSound()
                    setUserInfo(userInfo + builder.payloadData)
                    // Add image if available
                    builder.image?.let { notificationImage ->
                        val attachment = notificationImage.toNotificationAttachment()
                        attachment?.let {
                            setAttachments(listOf(it))
                        }
                    }
                }
                val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)
                val notificationRequest = UNNotificationRequest.requestWithIdentifier(
                    identifier = builder.id.toString(),
                    content = notificationContent,
                    trigger = trigger
                )
                notificationCenter.addNotificationRequest(notificationRequest) { error ->
                    error?.let { println("Error showing notification: $error") }
                }
            }
        }

    }


    override fun remove(id: Int) {
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(listOf(id.toString()))
    }

    override fun removeAll() {
        notificationCenter.removeAllDeliveredNotifications()
    }

    private fun UNMutableNotificationContent.setSound() {
        val customSoundPath = iosNotificationConfiguration.notificationSoundName
        val customNotificationSound = customSoundPath?.let {
            UNNotificationSound.soundNamed(customSoundPath)
        }
        val notificationSound = customNotificationSound ?: UNNotificationSound.defaultSound
        setSound(notificationSound)
    }

    @OptIn(ExperimentalForeignApi::class)
    private suspend fun NotificationImage.toNotificationAttachment(): UNNotificationAttachment? {
        return withContext(Dispatchers.IO) {
            try {
                when (this@toNotificationAttachment) {
                    is NotificationImage.Url -> {
                        val nsUrl = NSURL.URLWithString(url) ?: return@withContext null
                        val data = NSData.dataWithContentsOfURL(nsUrl)
                        val tempDirectory = NSTemporaryDirectory()
                        val tempFilePath =
                            tempDirectory + "/notification_image_${Random.nextInt()}.jpg"
                        val tempFileUrl = NSURL.fileURLWithPath(tempFilePath)
                        data?.writeToURL(tempFileUrl, true)
                        UNNotificationAttachment.attachmentWithIdentifier(
                            "notification_image",
                            tempFileUrl,
                            null,
                            null
                        )
                    }

                    is NotificationImage.File -> {
                        val fileUrl = NSURL.fileURLWithPath(path)
                        UNNotificationAttachment.attachmentWithIdentifier(
                            "notification_image",
                            fileUrl,
                            null,
                            null
                        )
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                println("Error creating notification attachment: $e")
                null
            }
        }
    }


    internal class NotificationDelegate : UNUserNotificationCenterDelegateProtocol, NSObject() {
        override fun userNotificationCenter(
            center: UNUserNotificationCenter,
            didReceiveNotificationResponse: UNNotificationResponse,
            withCompletionHandler: () -> Unit,
        ) {
            val notificationContent = didReceiveNotificationResponse.notification.request.content
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
            NotifierManager.onUserNotification(notificationContent)
            if (NotifierManager.shouldShowNotification(notificationContent)) withCompletionHandler(
                IosPermissionUtil.NOTIFICATION_PERMISSIONS
            )
        }
    }
}



