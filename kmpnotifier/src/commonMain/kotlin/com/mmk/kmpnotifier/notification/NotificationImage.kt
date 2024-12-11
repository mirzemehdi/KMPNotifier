package com.mmk.kmpnotifier.notification

public sealed class NotificationImage {
    /**
     * Url of image. Make sure you gave internet permission
     */
    public data class Url(val url: String) : NotificationImage()

    /**
     * File path. Make sure app can read this file
     */
    public data class File(val path: String) : NotificationImage()

}