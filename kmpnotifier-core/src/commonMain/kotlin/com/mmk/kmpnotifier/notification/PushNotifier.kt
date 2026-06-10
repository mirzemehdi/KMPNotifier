package com.mmk.kmpnotifier.notification

public typealias PayloadData = Map<String, *>

/**
 * Class represents push notification such as Firebase Push Notification
 */
public interface PushNotifier {

    /**
     * @return current push notification token
     */
    public suspend fun getToken(): String?

    /**
     * Deletes user push notification. For log out cases for example
     */
    public suspend fun deleteMyToken()

    /**
     * Subscribing user to group.
     * @param topic  Topic name
     */
    public suspend fun subscribeToTopic(topic: String)

    /**
     * Unsubscribe user from group.
     * @param topic  Topic name
     */
    public suspend fun unSubscribeFromTopic(topic: String)

}