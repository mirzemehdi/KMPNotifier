package com.mmk.kmpnotifier.notification

/**
 * Represents an action button that can be added to a notification
 */
public data class NotificationAction(
    /**
     * Unique identifier for this action
     */
    val id: String,

    /**
     * Display title for the action button
     */
    val title: String,

    val allowsTextInput: Boolean = false,
    val inputLabel: String? = null
)