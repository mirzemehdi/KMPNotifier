package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil
import kotlinx.browser.window
import org.w3c.notifications.Notification
import org.w3c.notifications.NotificationOptions
import kotlin.random.Random


@JsFun("() => typeof Notification !== 'undefined'")
private external fun isNotificationSupported(): Boolean
internal class WebConsoleNotifier(
    private val permissionUtil: PermissionUtil,
    private val configuration: NotificationPlatformConfiguration.Web
) : Notifier {

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
        if (isNotificationSupported().not()) {
            alertNotification(builder.body)
            return
        }
        permissionUtil.askNotificationPermission {
            permissionUtil.hasNotificationPermission { hasPermission ->
                if (hasPermission) showNotification(title = builder.title, body = builder.body)
                else alertNotification(builder.body)
            }
        }

    }

    override fun remove(id: Int) {
        println("remove notification is not implemented ")

    }

    override fun removeAll() {
        println("remove notification is not implemented ")
    }

    private fun showNotification(title: String, body: String) {
        val options = NotificationOptions(body = body, icon = configuration.notificationIconPath)
        Notification(title, options)
    }

    private fun alertNotification(message: String) {
        window.alert(message)
    }
}