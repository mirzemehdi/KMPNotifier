package com.mmk.kmpnotifier.notification

/**
 * Class that represent local notification
 */
public interface Notifier {

    /**
     * Sends local notification to device
     * @param title Title part
     * @param body Body part
     * @return notification id
     */
    public fun notify(title: String, body: String): Int

    /**
     * Sends local notification to device with id
     * @param id notification id
     * @param title Title part
     * @param body Body part
     */
    public fun notify(id:Int, title: String, body: String)



    /**
     * Remove notification by id
     * @param id notification id
     */
    public fun remove(id:Int)

    /**
     * Removes all previously shown notifications
     * @see remove(id) for removing specific notification.
     */
    public fun removeAll()
}