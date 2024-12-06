package com.mmk.kmpnotifier.notification

public sealed class NotificationImage {
    public data class Url(val url: String) : NotificationImage()
}