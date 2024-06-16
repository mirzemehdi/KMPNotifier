package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.extensions.getDesktopPlatformType
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.io.File
import kotlin.random.Random

internal class DesktopNotifier(private val desktopNotificationConfiguration: NotificationPlatformConfiguration.Desktop) :
    Notifier {
        init {
            println("CurrentDesktopPlatform: ${getDesktopPlatformType()}")
        }
    override fun notify(title: String, body: String, payloadData: Map<String, String>): Int {
        if (isTraySupported.not()) return -1

        val notificationID = Random.nextInt(0, Int.MAX_VALUE)
        notify(notificationID, title, body, payloadData)
        return notificationID
    }

    override fun notify(id: Int, title: String, body: String, payloadData: Map<String, String>) {
        if (isTraySupported.not()) return

        val iconPath = kotlin.runCatching {
            val resourcesDirectory = File(System.getProperty("compose.application.resources.dir"))
            val canonicalPath = resourcesDirectory.canonicalPath
            canonicalPath + File.separator + desktopNotificationConfiguration.notificationIconPath
        }.getOrNull()

        val icon = Toolkit.getDefaultToolkit().getImage(iconPath)
        val trayIcon = TrayIcon(icon).apply {
            isImageAutoSize = true
            addActionListener {
                println("onNotification action event $it")
            }
        }
        SystemTray.getSystemTray().add(trayIcon)
        trayIcon.displayMessage(title, body, TrayIcon.MessageType.INFO)

    }

    //TODO for now all notifications are deleted
    override fun remove(id: Int) {
        removeAll()
    }

    override fun removeAll() {
        val systemTray = SystemTray.getSystemTray()
        systemTray.trayIcons.forEach {
            systemTray.remove(it)
        }
    }

    private val isTraySupported: Boolean
        get() = SystemTray.isSupported().also {
            if (it.not()) System.err.println(
                "Tray is not supported on the current platform. " +
                        "Use the global property `isTraySupported` to check."
            )
        }


}