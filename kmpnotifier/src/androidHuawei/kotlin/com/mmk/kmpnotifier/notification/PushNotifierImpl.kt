package com.mmk.kmpnotifier.notification

import com.huawei.hmf.tasks.Task
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.push.HmsMessaging
import com.mmk.kmpnotifier.di.applicationContext
import com.mmk.kmpnotifier.logger.currentLogger
import com.mmk.kmpnotifier.logger.log
import com.huawei.hmf.tasks.Tasks
import com.huawei.hms.common.util.AGCUtils


internal class PushNotifierImpl : PushNotifier() {

    init {
        currentLogger.log("PushNotifier is initialized")
    }

    private val instanceId by lazy { HmsInstanceId.getInstance(applicationContext) }
    private val messaging by lazy { HmsMessaging.getInstance(applicationContext) }
    private val appId by lazy {
        AGCUtils.getAppId(applicationContext) ?: throw IllegalStateException("Huawei App ID is not provided")
    }

    override suspend fun getToken(): String? = callSafe(
        onFailure = { currentLogger.log("Error while getting token: $it") },
        block = { instanceId.getToken(appId, HmsMessaging.DEFAULT_TOKEN_SCOPE) }
    ).getOrNull()

    override suspend fun deleteMyToken() = callSafe(
        onFailure = { currentLogger.log("Error while deleting token: $it") },
        block = { instanceId.deleteToken(appId, HmsMessaging.DEFAULT_TOKEN_SCOPE) }
    ).isSuccess

    override suspend fun subscribeToTopic(topic: String) = messaging.subscribe(topic)
        .await { currentLogger.log("Error while subscribing to topic($topic): $it") }
        .isSuccess

    override suspend fun unSubscribeFromTopic(topic: String) = messaging.unsubscribe(topic)
        .await { currentLogger.log("Error while unsubscribing from topic($topic): $it") }
        .isSuccess

    private fun <T> Task<T>.await(onFailure: (Throwable) -> Unit = currentLogger::log) =
        callSafe(onFailure = onFailure) { Tasks.await<T>(this) }
}
