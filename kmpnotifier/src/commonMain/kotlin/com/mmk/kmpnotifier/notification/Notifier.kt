package com.mmk.kmpnotifier.notification

/**
 * Class that represent local notification
 */
public interface Notifier {

    /**
     * Sends local notification to device
     * @param title Title part
     * @param body Body part
     */
    public fun notify(title: String, body: String)
}