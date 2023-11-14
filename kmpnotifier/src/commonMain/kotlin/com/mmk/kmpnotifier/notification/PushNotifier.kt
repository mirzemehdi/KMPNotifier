package com.mmk.kmpnotifier.notification

public interface PushNotifier {


    public suspend fun getToken(): String?
    public suspend fun deleteMyToken()
    public suspend fun subscribeToTopic(topic: String)
    public suspend fun unSubscribeFromTopic(topic: String)

    public fun doAfterInitialization() {}
}