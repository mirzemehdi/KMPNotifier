package com.mmk.kmpnotifier.extensions

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.NotifierManagerImpl

public fun NotifierManager.onApplicationDidReceiveRemoteNotification(userInfo:Map<Any?, *>){
    val payloadData = userInfo.keys
        .filterNotNull()
        .filterIsInstance<String>()
        .associateWith { key -> userInfo[key] }

    if (payloadData.containsKey("gcm.message_id")) NotifierManagerImpl.onPushPayloadData(payloadData)
}