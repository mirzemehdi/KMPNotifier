@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.extensions.asPayloadData
import com.mmk.kmpnotifier.extensions.emitPushEventsIfRemote
import com.mmk.kmpnotifier.extensions.isRemoteNotification
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierEventHub
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.logger.currentLogger
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
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
import platform.UserNotifications.UNTextInputNotificationResponse
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random


internal const val IOS_ACTION_SCHEDULED_FIRED = "com.mmk.kmpnotifier.EVENT_SCHEDULED_NOTIFICATION_FIRED_INTERNAL"
internal const val IOS_ACTION_NOTIFICATION_ACTION = "com.mmk.kmpnotifier.NOTIFICATION_ACTION"
internal const val IOS_EXTRA_ACTION_ID = "action_id"
internal const val IOS_EXTRA_NOTIFICATION_ID = "notification_id"

/**
 * Options used when presenting a foreground notification.
 * Kept bit-identical to the authorization options the library historically passed here.
 */
internal val IOS_PRESENTATION_OPTIONS =
    UNAuthorizationOptionAlert or
            UNAuthorizationOptionSound or
            UNAuthorizationOptionBadge


internal class IosNotifier(
    private val permissionUtil: PermissionUtil,
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
                // Register the action category before adding the request, so the buttons are
                // known when the notification is presented.
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

    private suspend fun registerActionCategory(notificationId: Int, actions: List<NotificationAction>) {
        val iosActions = actions.map { it.toIosAction() }
        val category = UNNotificationCategory.categoryWithIdentifier(
            identifier = actionCategoryId(notificationId),
            actions = iosActions,
            intentIdentifiers = emptyList<String>(),
            options = 0u
        )
        // Get existing categories and replace any previous one for this notification id
        val existingCategories = suspendCoroutine { continuation ->
            notificationCenter.getNotificationCategoriesWithCompletionHandler { continuation.resume(it) }
        }
        val updatedCategories = existingCategories.orEmpty()
            .filterIsInstance<UNNotificationCategory>()
            .filterNot { it.identifier == category.identifier }
            .toSet() + category
        notificationCenter.setNotificationCategories(updatedCategories)
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

    private suspend fun NotificationImage.toNotificationAttachment(): UNNotificationAttachment? {
        return withContext(Dispatchers.IO) {
            try {
                val sourceUrl = when (this@toNotificationAttachment) {
                    is NotificationImage.Url -> NSURL.URLWithString(url)
                    is NotificationImage.File -> NSURL.fileURLWithPath(path)
                } ?: return@withContext null
                val data = NSData.dataWithContentsOfURL(sourceUrl) ?: run {
                    currentLogger.log("Could not load notification image: $sourceUrl")
                    return@withContext null
                }
                createImageAttachment(data, extensionHint = sourceUrl.pathExtension)
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

                NotifierEventHub.emitAction(
                    actionId = actionIdentifier,
                    notificationId = notificationId,
                    payload = payload
                )
            }

            emitPushEventsIfRemote(notificationContent)
            NotifierEventHub.emitNotificationClicked(notificationContent.userInfo.asPayloadData())
            withCompletionHandler()
        }

        // Asks the delegate how to handle a notification that arrived while the app was running
        //  in the foreground.
        override fun userNotificationCenter(
            center: UNUserNotificationCenter,
            willPresentNotification: UNNotification,
            withCompletionHandler: (UNNotificationPresentationOptions) -> Unit,
        ) {
            val request = willPresentNotification.request
            val notificationContent = request.content
            emitPushEventsIfRemote(notificationContent)
            // Parity with Android, which emits this when the scheduled alarm fires. iOS delivers
            // scheduled notifications itself, so this is only observable while in the foreground.
            if (!notificationContent.isRemoteNotification() && request.trigger.isScheduledLocalTrigger()) {
                NotifierEventHub.emitAction(
                    actionId = IOS_ACTION_SCHEDULED_FIRED,
                    notificationId = request.identifier.toIntOrNull() ?: 0,
                    payload = notificationContent.userInfo.asPayloadData()
                )
            }
            if (shouldShowNotification(notificationContent)) {
                withCompletionHandler(IOS_PRESENTATION_OPTIONS)
            } else {
                // UNUserNotificationCenter expects the handler to be called exactly once;
                // pass no options to suppress the presentation.
                withCompletionHandler(0u)
            }
        }

        private fun shouldShowNotification(notificationContent: platform.UserNotifications.UNNotificationContent): Boolean {
            val configuration =
                NotifierInternals.configurationOrNull() as? NotificationPlatformConfiguration.Ios
            val showPushNotificationEnabled = configuration?.showPushNotification ?: true
            return when {
                notificationContent.isRemoteNotification() && !showPushNotificationEnabled -> false
                else -> true
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

