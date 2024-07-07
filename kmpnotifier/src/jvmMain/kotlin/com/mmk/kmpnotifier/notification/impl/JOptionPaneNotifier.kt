package com.mmk.kmpnotifier.notification.impl

import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import javax.swing.ImageIcon
import javax.swing.JOptionPane

internal class JOptionPaneNotifier(private val configuration: NotificationPlatformConfiguration.Desktop) :
    Notifier {

    override fun notify(title: String, body: String, payloadData: Map<String, String>): Int {
        val id = -1
        notify(id = id, title = title, body = body, payloadData)
        return id
    }

    override fun notify(id: Int, title: String, body: String, payloadData: Map<String, String>) {
        JOptionPane.showMessageDialog(
            null,
            body,
            title,
            JOptionPane.INFORMATION_MESSAGE,
            ImageIcon(configuration.notificationIconPath)
        )
    }

    override fun remove(id: Int) {
        println("No remove functionality")
    }

    override fun removeAll() {
        println("No removeAll functionality")
    }
}