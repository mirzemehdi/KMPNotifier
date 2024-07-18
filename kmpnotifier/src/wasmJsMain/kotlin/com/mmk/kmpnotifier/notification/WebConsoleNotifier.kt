package com.mmk.kmpnotifier.notification

internal class WebConsoleNotifier:Notifier {
    override fun notify(title: String, body: String, payloadData: Map<String, String>): Int {
        println("Notify is called")
        return -1
    }

    override fun notify(id: Int, title: String, body: String, payloadData: Map<String, String>) {
        println("Notify is called")

    }

    override fun remove(id: Int) {
        println("remove is called")

    }

    override fun removeAll() {
        println("removeAll is called")

    }
}