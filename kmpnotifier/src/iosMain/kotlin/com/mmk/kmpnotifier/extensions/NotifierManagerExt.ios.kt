package com.mmk.kmpnotifier.extensions

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.NotifierManagerImpl

/***
 * In order to receive notification data payload this functions needs to be called in
 * ios Swift side application didReceiveRemoteNotification function
 *
 * Example:
 *
 * ```
 * func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any]) async -> UIBackgroundFetchResult {
 *   NotifierManager.shared.onApplicationDidReceiveRemoteNotification(userInfo: userInfo)
 *   return UIBackgroundFetchResult.newData
 * }
 * ```
 */
public fun NotifierManager.onApplicationDidReceiveRemoteNotification(userInfo:Map<Any?, *>){
    val payloadData = userInfo.keys
        .filterNotNull()
        .filterIsInstance<String>()
        .associateWith { key -> userInfo[key] }

    if (payloadData.containsKey("gcm.message_id")) NotifierManagerImpl.onPushPayloadData(payloadData)
}