package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.permission.PermissionUtil
import kotlinx.browser.window
import org.w3c.notifications.Notification
import org.w3c.notifications.NotificationOptions
import kotlin.random.Random


internal class WebConsoleNotifier(private val permissionUtil: PermissionUtil) : Notifier {

    override fun notify(title: String, body: String, payloadData: Map<String, String>): Int {
        val notificationID = Random.nextInt(0, Int.MAX_VALUE)
        notify(notificationID, title, body, payloadData)
        return notificationID
    }


    override fun notify(id: Int, title: String, body: String, payloadData: Map<String, String>) {
        if (isNotificationSupported().not()) {
            alertNotification(body)
            return
        }
        permissionUtil.askNotificationPermission {
            permissionUtil.hasNotificationPermission { hasPermission ->
                if (hasPermission) showNotification(title = title, body = body)
                else alertNotification(body)
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
        val options = NotificationOptions(body = body)
        Notification(title, options)
    }

    private fun alertNotification(message: String) {
        window.alert(message)
    }

    private fun isNotificationSupported(): Boolean{
        return js("typeof Notification !== 'undefined'").unsafeCast<Boolean>()
    }

}