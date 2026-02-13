package com.mmk.kmpnotifier.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.mmk.kmpnotifier.Constants.ACTION_NOTIFICATION_CLICK
import com.mmk.kmpnotifier.extensions.notificationManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

public const val ACTION_SCHEDULED_NOTIFICATION_FIRED: String =
    "com.mmk.kmpnotifier.EVENT_SCHEDULED_NOTIFICATION_FIRED_INTERNAL"
public const val ACTION_SCHEDULED_NOTIFICATION_FIRED_PUBLIC: String =
    "com.mmk.kmpnotifier.EVENT_SCHEDULED_NOTIFICATION_FIRED"


internal class AndroidNotifier(
    private val context: Context,
    private val androidNotificationConfiguration: NotificationPlatformConfiguration.Android,
    private val notificationChannelFactory: NotificationChannelFactory,
    private val permissionUtil: PermissionUtil,
) : Notifier {

    private val scope by lazy { MainScope() }

    private val alarmManager by lazy { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    companion object {
        internal const val ACTION_NOTIFICATION_ACTION = "com.mmk.kmpnotifier.NOTIFICATION_ACTION"
        internal const val EXTRA_ACTION_ID = "action_id"
        internal const val EXTRA_NOTIFICATION_ID = "notification_id"
        internal const val EXTRA_REMOTE_INPUT = "extra_remote_input"

    }


    override fun notify(title: String, body: String, payloadData: Map<String, String>): Int {
        val notificationID = Random.nextInt(0, Int.MAX_VALUE)
        notify {
            this.id = notificationID
            this.title = title
            this.body = body
            this.payloadData = payloadData
        }
        return notificationID
    }

    override fun notify(id: Int, title: String, body: String, payloadData: Map<String, String>) {
        notify {
            this.id = id
            this.title = title
            this.body = body
            this.payloadData = payloadData
        }
    }

    override fun notify(block: NotifierBuilder.() -> Unit) {
        val builder = NotifierBuilder().apply(block)
        permissionUtil.hasNotificationPermission {
            if (it.not())
                Log.w(
                    "AndroidNotifier", "You need to ask runtime " +
                            "notification permission (Manifest.permission.POST_NOTIFICATIONS) in your activity"
                )
        }
        val notificationManager = context.notificationManager ?: return
        val pendingIntent = getPendingIntent(builder.payloadData, builder.id)
        notificationChannelFactory.createChannels()

        if (isScheduledNotification(builder.scheduledAt)) {
            scheduleNotificationAlarm(builder)
            return
        }

        scope.launch {
            val imageBitmap = builder.image?.asBitmap()
            val notification = NotificationCompat.Builder(
                context,
                androidNotificationConfiguration.notificationChannelData.id
            ).apply {
                setChannelId(androidNotificationConfiguration.notificationChannelData.id)
                setContentTitle(builder.title)
                setContentText(builder.body)
                imageBitmap?.let {
                    setLargeIcon(it)
                    setStyle(
                        NotificationCompat.BigPictureStyle()
                            .bigPicture(it)
                            .bigLargeIcon(null as Bitmap?)
                    )
                }
                setSmallIcon(androidNotificationConfiguration.notificationIconResId)
                setAutoCancel(true)
                setOnlyAlertOnce(true)
                setContentIntent(pendingIntent)
                androidNotificationConfiguration.notificationIconColorResId?.let {
                    color = ContextCompat.getColor(context, it)
                }

                // Add action buttons
                builder.actions.forEach { action ->
                    val actionPendingIntent =
                        createActionPendingIntent(
                            actionId = action.id,
                            notificationId = builder.id,
                            payload = builder.payloadData
                        )

                    val actionBuilder = NotificationCompat.Action.Builder(
                        0,
                        action.title,
                        actionPendingIntent
                    )

                    if (action.allowsTextInput) {
                        val remoteInput = androidx.core.app.RemoteInput.Builder(EXTRA_REMOTE_INPUT)
                            .setLabel(action.inputLabel ?: "Enter value")
                            .build()

                        actionBuilder.addRemoteInput(remoteInput)
                    }

                    addAction(actionBuilder.build())
                }

            }.build()
            notificationManager.notify(builder.id, notification)
        }


    }

    override fun remove(id: Int) {
        val notificationManager = context.notificationManager ?: return


        // Cancel any scheduled alarm
        val intent = Intent(ACTION_SCHEDULED_NOTIFICATION_FIRED)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }

        notificationManager.cancel(id)
    }

    override fun removeAll() {
        val notificationManager = context.notificationManager ?: return
        notificationManager.cancelAll()
    }

    private fun isScheduledNotification(scheduledAt: Long): Boolean {
        if (scheduledAt == 0L) return false
        val currentTime = System.currentTimeMillis()

        // Handle relative timing: if fireAt is a small value (less than a reasonable timestamp),
        // treat it as milliseconds from now
        val actualFireTime = if (scheduledAt < 1000000000000L) { // Before year 2001, treat as relative
            currentTime + scheduledAt
        } else {
            scheduledAt
        }

        return actualFireTime > currentTime
    }

    private fun scheduleNotificationAlarm(request: NotifierBuilder) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_SCHEDULED_NOTIFICATION_FIRED
            putExtra(EXTRA_ACTION_ID, ACTION_SCHEDULED_NOTIFICATION_FIRED)
            putExtra(EXTRA_NOTIFICATION_ID, request.id)
            request.payloadData.forEach { putExtra(it.key, it.value) }
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            request.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                request.scheduledAt,
                pendingIntent
            )
            Log.d("AndroidNotificationScheduler", "Scheduled notification ${request.id} for ${request.scheduledAt}")
        } catch (e: SecurityException) {
            Log.e("AndroidNotificationScheduler", "Failed to schedule exact alarm", e)
            // Fallback to inexact alarm
            alarmManager.set(AlarmManager.RTC_WAKEUP, request.scheduledAt, pendingIntent)
        }
    }


    private fun getPendingIntent(payloadData: Map<String, String>, id: Int): PendingIntent? {
        val intent = getLauncherActivityIntent()?.apply {
            putExtra(ACTION_NOTIFICATION_CLICK, ACTION_NOTIFICATION_CLICK)
            payloadData.forEach { putExtra(it.key, it.value) }
            val urlData = payloadData.getOrDefault(Notifier.KEY_URL, null)
            urlData?.let { setData(Uri.parse(urlData)) }
        }
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT


        return PendingIntent.getActivity(context, id, intent, flags)
    }

    private fun getLauncherActivityIntent(): Intent? {
        val packageManager = context.applicationContext.packageManager
        return packageManager.getLaunchIntentForPackage(context.applicationContext.packageName)
    }

    private fun createActionPendingIntent(
        actionId: String,
        notificationId: Int,
        payload: Map<String, String>
    ): PendingIntent {

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_NOTIFICATION_ACTION
            putExtra(EXTRA_ACTION_ID, actionId)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            payload.forEach { putExtra(it.key, it.value) }
        }


        val flags = PendingIntent.FLAG_UPDATE_CURRENT or FLAG_MUTABLE
        return PendingIntent.getBroadcast(context, (actionId + notificationId).hashCode(), intent, flags)
    }

    private suspend fun NotificationImage?.asBitmap(): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                when (this@asBitmap) {
                    null -> null
                    is NotificationImage.Url -> {
                        URL(url).openStream().buffered().use { inputStream ->
                            BitmapFactory.decodeStream(inputStream)
                        }
                    }

                    is NotificationImage.File -> {
                        BitmapFactory.decodeFile(path)
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e(
                    "AndroidNotifier",
                    "Error while processing notification image. Ensure correct path or internet connection.",
                    e
                )
                null
            }
        }
    }


}