package com.mmk.kmpnotifier.notification.impl

import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import kotlin.random.Random

internal class TrayNotifier(private val configuration: NotificationPlatformConfiguration.Desktop) :
    Notifier {

    private val trayIcons: MutableMap<Int, TrayIcon> = mutableMapOf()

    companion object {
        val isSupported by lazy {
            SystemTray.isSupported().also {
                if (it.not()) System.err.println(
                    "Tray is not supported on the current platform. "
                )
            }
        }
    }

    override fun notify(title: String, body: String, payloadData: Map<String, String>): Int {
        if (isSupported.not()) return -1
        val notificationID = Random.nextInt(0, Int.MAX_VALUE)
        notify(notificationID, title, body, payloadData)
        return notificationID
    }

    override fun notify(
        id: Int,
        title: String,
        body: String,
        payloadData: Map<String, String>
    ) {
        if (isSupported.not()) return
        val icon = Toolkit.getDefaultToolkit().getImage(configuration.notificationIconPath)
        val trayIcon = TrayIcon(icon).apply {
            isImageAutoSize = true
        }
        SystemTray.getSystemTray().add(trayIcon)
            .also { trayIcons[id] = trayIcon }
        trayIcon.displayMessage(title, body, TrayIcon.MessageType.INFO)
    }

    override fun remove(id: Int) {
        val systemTray = SystemTray.getSystemTray()
        val trayIcon = trayIcons.getOrDefault(id, null)
        trayIcon?.let { systemTray.remove(it) }
    }

    override fun removeAll() {
        val systemTray = SystemTray.getSystemTray()
        systemTray.trayIcons.forEach { systemTray.remove(it) }
    }
}