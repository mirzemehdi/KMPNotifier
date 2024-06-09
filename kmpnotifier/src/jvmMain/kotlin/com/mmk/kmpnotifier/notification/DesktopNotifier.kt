package com.mmk.kmpnotifier.notification

internal class DesktopNotifier:Notifier {
    override fun notify(title: String, body: String, payloadData: Map<String, String>): Int {
        println("$title, $body")
        return 1
    }

    override fun notify(id: Int, title: String, body: String, payloadData: Map<String, String>) {
        println("$title, $body")

    }

    override fun remove(id: Int) {
        println("removed")

    }

    override fun removeAll() {
        println("remove all")

    }
}