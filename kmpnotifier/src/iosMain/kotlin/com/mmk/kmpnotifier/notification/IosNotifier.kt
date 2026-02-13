package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.extensions.asPayloadData
import com.mmk.kmpnotifier.extensions.onNotificationClicked
import com.mmk.kmpnotifier.extensions.onUserNotification
import com.mmk.kmpnotifier.extensions.shouldShowNotification
import com.mmk.kmpnotifier.logger.currentLogger
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.IosPermissionUtil
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSDate
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.writeToURL
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationAttachment
import platform.UserNotifications.UNNotificationCategory
import platform.UserNotifications.UNNotificationDefaultActionIdentifier
import platform.UserNotifications.UNNotificationDismissActionIdentifier
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationResponse
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNNotificationTrigger
import platform.UserNotifications.UNTextInputNotificationResponse
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random


internal const val IOS_ACTION_SCHEDULED_FIRED = "com.mmk.kmpnotifier.EVENT_SCHEDULED_NOTIFICATION_FIRED_INTERNAL"
internal const val IOS_ACTION_NOTIFICATION_ACTION = "com.mmk.kmpnotifier.NOTIFICATION_ACTION"
internal const val IOS_EXTRA_ACTION_ID = "action_id"
internal const val IOS_EXTRA_NOTIFICATION_ID = "notification_id"


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
                // Register action category if actions are provided
                if (builder.actions.isNotEmpty()) {
                    registerActionCategory(builder.id, builder.actions)
                }

                val notificationContent = UNMutableNotificationContent().apply {
                    setTitle(builder.title)
                    setBody(builder.body)
                    setSound()
                    setUserInfo(userInfo + builder.payloadData)
                    // Set category for action buttons
                    if (builder.actions.isNotEmpty()) {
                        setCategoryIdentifier(actionCategoryId(builder.id))
                    }
                    // Add image if available
                    builder.image?.let { notificationImage ->
                        val attachment = notificationImage.toNotificationAttachment()
                        attachment?.let {
                            setAttachments(listOf(it))
                        }
                    }
                }

                val trigger = createTrigger(builder.scheduledAt)
                val notificationRequest = UNNotificationRequest.requestWithIdentifier(
                    identifier = builder.id.toString(),
                    content = notificationContent,
                    trigger = trigger
                )
                notificationCenter.addNotificationRequest(notificationRequest) { error ->
                    error?.let { currentLogger.log("Error showing notification: $error") }
                }
            }
        }

    }

    private fun actionCategoryId(notificationId: Int): String {
        return "com.mmk.kmpnotifier.actions.$notificationId"
    }

    private fun registerActionCategory(notificationId: Int, actions: List<NotificationAction>) {
        val iosActions = actions.map { it.toIosAction() }
        val category = UNNotificationCategory.categoryWithIdentifier(
            identifier = actionCategoryId(notificationId),
            actions = iosActions,
            intentIdentifiers = emptyList<String>(),
            options = 0u
        )
        // Get existing categories and add the new one
        notificationCenter.getNotificationCategoriesWithCompletionHandler { existingCategories ->
            @Suppress("UNCHECKED_CAST")
            val updatedCategories = (existingCategories as? Set<UNNotificationCategory>)
                .orEmpty()
                .toMutableSet()
            updatedCategories.add(category)
            notificationCenter.setNotificationCategories(updatedCategories)
        }
    }

    private fun createTrigger(scheduledAt: Long): UNNotificationTrigger {
        if (scheduledAt == 0L) {
            return UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)
        }

        val currentTimeMs = (NSDate().timeIntervalSince1970 * 1000).toLong()

        // Handle relative timing: if scheduledAt is a small value (less than a reasonable timestamp),
        // treat it as milliseconds from now
        val actualFireTimeMs = if (scheduledAt < 1000000000000L) {
            currentTimeMs + scheduledAt
        } else {
            scheduledAt
        }

        val delayMs = actualFireTimeMs - currentTimeMs
        if (delayMs <= 0) {
            return UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)
        }

        val delaySeconds = delayMs / 1000.0
        return UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(delaySeconds, false)
    }


    override fun remove(id: Int) {
        val identifier = listOf(id.toString())
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(identifier)
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(identifier)
    }

    override fun removeAll() {
        notificationCenter.removeAllDeliveredNotifications()
        notificationCenter.removeAllPendingNotificationRequests()
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
                currentLogger.log("Error creating notification attachment: $e")
                null
            }
        }
    }


    internal class NotificationDelegate : UNUserNotificationCenterDelegateProtocol, NSObject() {
        // This is called for you to handle the tapped on notification action
        override fun userNotificationCenter(
            center: UNUserNotificationCenter,
            didReceiveNotificationResponse: UNNotificationResponse,
            withCompletionHandler: () -> Unit,
        ) {
            val notificationContent = didReceiveNotificationResponse.notification.request.content
            val actionIdentifier = didReceiveNotificationResponse.actionIdentifier

            // Handle custom action button taps (not default tap or dismiss)
            if (actionIdentifier != UNNotificationDefaultActionIdentifier &&
                actionIdentifier != UNNotificationDismissActionIdentifier
            ) {
                val notificationId = didReceiveNotificationResponse.notification.request.identifier
                    .toIntOrNull() ?: 0
                val payload = notificationContent.userInfo.asPayloadData().toMutableMap()

                // Extract text input if this is a text input action response
                if (didReceiveNotificationResponse is UNTextInputNotificationResponse) {
                    val userText = didReceiveNotificationResponse.userText
                    payload["remote_input"] = userText
                }

                NotifierManagerImpl.onAction(
                    actionId = actionIdentifier,
                    notificationId = notificationId,
                    payload = payload
                )
            }

            NotifierManager.onUserNotification(notificationContent)
            NotifierManager.onNotificationClicked(notificationContent)
            withCompletionHandler()
        }

        // Asks the delegate how to handle a notification that arrived while the app was running
        //  in the foreground.
        override fun userNotificationCenter(
            center: UNUserNotificationCenter,
            willPresentNotification: UNNotification,
            withCompletionHandler: (UNNotificationPresentationOptions) -> Unit,
        ) {
            val notificationContent = willPresentNotification.request.content
            NotifierManager.onUserNotification(notificationContent)
            if (NotifierManager.shouldShowNotification(notificationContent)) {
                withCompletionHandler(IosPermissionUtil.NOTIFICATION_PERMISSIONS)
            }
        }
    }
}

private fun NotificationAction.toIosAction(): platform.UserNotifications.UNNotificationAction {
    return if (allowsTextInput) {
        platform.UserNotifications.UNTextInputNotificationAction.actionWithIdentifier(
            identifier = id,
            title = title,
            options = 0u,
            textInputButtonTitle = "Send",
            textInputPlaceholder = inputLabel ?: ""
        )
    } else {
        platform.UserNotifications.UNNotificationAction.actionWithIdentifier(
            identifier = id,
            title = title,
            options = 0u
        )
    }
}

