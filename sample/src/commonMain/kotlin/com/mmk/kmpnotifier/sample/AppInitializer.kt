package com.mmk.kmpnotifier.sample

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData


object AppInitializer {
    fun onApplicationStart(){
        onApplicationStartPlatformSpecific()
        NotifierManager.addListener(object : NotifierManager.Listener {
            override fun onNewToken(token: String) {
                println("Push Notification onNewToken: $token")
            }

            override fun onPayloadData(data: PayloadData) {
                super.onPayloadData(data)
                println("Push Notification payloadData: $data")
            }
        })
    }
}