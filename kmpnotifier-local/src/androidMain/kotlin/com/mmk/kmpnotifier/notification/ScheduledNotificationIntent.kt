package com.mmk.kmpnotifier.notification

import android.content.Intent
import android.os.Bundle
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

/**
 * Encodes a scheduled [NotifierBuilder] (and the Android configuration needed to display it)
 * into the alarm intent, so [NotificationReceiver] can post the notification even when the
 * alarm fires in a cold process before `KMPNotifier.initialize` has run.
 *
 * All keys share [PREFIX] and are stripped from the payload emitted to listeners.
 */
internal object ScheduledNotificationIntent {

    private const val PREFIX = "com.mmk.kmpnotifier.scheduled."
    private const val EXTRA_TITLE = PREFIX + "title"
    private const val EXTRA_BODY = PREFIX + "body"
    private const val EXTRA_PAYLOAD = PREFIX + "payload"
    private const val EXTRA_IMAGE_URL = PREFIX + "image_url"
    private const val EXTRA_IMAGE_FILE = PREFIX + "image_file"
    private const val EXTRA_ACTION_IDS = PREFIX + "action_ids"
    private const val EXTRA_ACTION_TITLES = PREFIX + "action_titles"
    private const val EXTRA_ACTION_TEXT_INPUT = PREFIX + "action_text_input"
    private const val EXTRA_ACTION_INPUT_LABELS = PREFIX + "action_input_labels"
    private const val EXTRA_ICON_RES_ID = PREFIX + "icon_res_id"
    private const val EXTRA_ICON_COLOR_RES_ID = PREFIX + "icon_color_res_id"
    private const val EXTRA_CHANNEL_ID = PREFIX + "channel_id"
    private const val EXTRA_CHANNEL_NAME = PREFIX + "channel_name"
    private const val EXTRA_CHANNEL_DESCRIPTION = PREFIX + "channel_description"
    private const val EXTRA_CHANNEL_SOUND_URI = PREFIX + "channel_sound_uri"

    fun isInternalKey(key: String): Boolean = key.startsWith(PREFIX)

    fun putNotification(
        intent: Intent,
        builder: NotifierBuilder,
        configuration: NotificationPlatformConfiguration.Android,
    ) {
        intent.putExtra(EXTRA_TITLE, builder.title)
        intent.putExtra(EXTRA_BODY, builder.body)
        intent.putExtra(EXTRA_PAYLOAD, Bundle().apply {
            builder.payloadData.forEach { (key, value) -> putString(key, value) }
        })
        when (val image = builder.image) {
            is NotificationImage.Url -> intent.putExtra(EXTRA_IMAGE_URL, image.url)
            is NotificationImage.File -> intent.putExtra(EXTRA_IMAGE_FILE, image.path)
            null -> Unit
        }
        if (builder.actions.isNotEmpty()) {
            intent.putExtra(EXTRA_ACTION_IDS, builder.actions.map { it.id }.toTypedArray())
            intent.putExtra(EXTRA_ACTION_TITLES, builder.actions.map { it.title }.toTypedArray())
            intent.putExtra(EXTRA_ACTION_TEXT_INPUT, builder.actions.map { it.allowsTextInput }.toBooleanArray())
            intent.putExtra(EXTRA_ACTION_INPUT_LABELS, builder.actions.map { it.inputLabel }.toTypedArray())
        }

        intent.putExtra(EXTRA_ICON_RES_ID, configuration.notificationIconResId)
        configuration.notificationIconColorResId?.let { intent.putExtra(EXTRA_ICON_COLOR_RES_ID, it) }
        val channel = configuration.notificationChannelData
        intent.putExtra(EXTRA_CHANNEL_ID, channel.id)
        intent.putExtra(EXTRA_CHANNEL_NAME, channel.name)
        intent.putExtra(EXTRA_CHANNEL_DESCRIPTION, channel.description)
        channel.soundUri?.let { intent.putExtra(EXTRA_CHANNEL_SOUND_URI, it) }
    }

    /** Returns the scheduled notification, or null for alarms set by versions that didn't store it. */
    fun readNotification(intent: Intent, notificationId: Int): NotifierBuilder? {
        if (!intent.hasExtra(EXTRA_TITLE)) return null
        return NotifierBuilder().apply {
            id = notificationId
            title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
            body = intent.getStringExtra(EXTRA_BODY).orEmpty()
            payloadData = intent.getBundleExtra(EXTRA_PAYLOAD)?.let { bundle ->
                bundle.keySet().mapNotNull { key -> bundle.getString(key)?.let { key to it } }.toMap()
            }.orEmpty()
            image = intent.getStringExtra(EXTRA_IMAGE_URL)?.let { NotificationImage.Url(it) }
                ?: intent.getStringExtra(EXTRA_IMAGE_FILE)?.let { NotificationImage.File(it) }
            actions = readActions(intent)
        }
    }

    /** Configuration captured when the alarm was scheduled; used when the library isn't initialized. */
    fun readConfiguration(intent: Intent): NotificationPlatformConfiguration.Android? {
        val iconResId = intent.getIntExtra(EXTRA_ICON_RES_ID, 0)
        if (iconResId == 0) return null
        val defaults = NotificationPlatformConfiguration.Android.NotificationChannelData()
        return NotificationPlatformConfiguration.Android(
            notificationIconResId = iconResId,
            notificationIconColorResId = intent.getIntExtra(EXTRA_ICON_COLOR_RES_ID, 0).takeIf { it != 0 },
            notificationChannelData = NotificationPlatformConfiguration.Android.NotificationChannelData(
                id = intent.getStringExtra(EXTRA_CHANNEL_ID) ?: defaults.id,
                name = intent.getStringExtra(EXTRA_CHANNEL_NAME) ?: defaults.name,
                description = intent.getStringExtra(EXTRA_CHANNEL_DESCRIPTION) ?: defaults.description,
                soundUri = intent.getStringExtra(EXTRA_CHANNEL_SOUND_URI),
            ),
        )
    }

    private fun readActions(intent: Intent): List<NotificationAction> {
        val ids = intent.getStringArrayExtra(EXTRA_ACTION_IDS) ?: return emptyList()
        val titles = intent.getStringArrayExtra(EXTRA_ACTION_TITLES) ?: return emptyList()
        val textInput = intent.getBooleanArrayExtra(EXTRA_ACTION_TEXT_INPUT)
        val inputLabels = intent.getStringArrayExtra(EXTRA_ACTION_INPUT_LABELS)
        return ids.indices.mapNotNull { index ->
            NotificationAction(
                id = ids[index],
                title = titles.getOrNull(index) ?: return@mapNotNull null,
                allowsTextInput = textInput?.getOrNull(index) ?: false,
                inputLabel = inputLabels?.getOrNull(index),
            )
        }
    }
}
