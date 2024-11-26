package com.mmk.kmpnotifier.notification

/**
 * Provides all data required for the Android notification
 */
public interface AndroidNotificationManager {
    /**
     * Provide Android notification data
     * @param notificationData - data required for displaying notification
     */
    public fun setData(notificationData: AndroidNotificationData)
}