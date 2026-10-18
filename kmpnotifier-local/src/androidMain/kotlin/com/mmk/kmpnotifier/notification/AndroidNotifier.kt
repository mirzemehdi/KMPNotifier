@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.mmk.kmpnotifier.extensions.notificationManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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

    private val poster = AndroidNotificationPoster(
        context = context,
        androidNotificationConfiguration = androidNotificationConfiguration,
        notificationChannelFactory = notificationChannelFactory,
    )

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
        if (context.notificationManager == null) return

        val fireTime = resolveFireTime(builder.scheduledAt)
        if (fireTime != null) {
            notificationChannelFactory.createChannels()
            scheduleNotificationAlarm(builder, fireTime)
            return
        }

        scope.launch { poster.post(builder) }
    }

    override fun remove(id: Int) {
        val notificationManager = context.notificationManager ?: return


        // Cancel any scheduled alarm. The intent must match the scheduled one
        // (action + component) for FLAG_NO_CREATE to find the PendingIntent.
        val intent = scheduledAlarmIntent()
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

    /** Absolute fire time for a future [scheduledAt], or null to show immediately. */
    private fun resolveFireTime(scheduledAt: Long): Long? {
        if (scheduledAt == 0L) return null
        val currentTime = System.currentTimeMillis()

        // Handle relative timing: if fireAt is a small value (less than a reasonable timestamp),
        // treat it as milliseconds from now
        val actualFireTime = if (scheduledAt < 1000000000000L) { // Before year 2001, treat as relative
            currentTime + scheduledAt
        } else {
            scheduledAt
        }

        return actualFireTime.takeIf { it > currentTime }
    }

    private fun scheduledAlarmIntent(): Intent =
        Intent(context, NotificationReceiver::class.java).setAction(ACTION_SCHEDULED_NOTIFICATION_FIRED)

    private fun scheduleNotificationAlarm(request: NotifierBuilder, fireTime: Long) {
        val intent = scheduledAlarmIntent().apply {
            putExtra(EXTRA_ACTION_ID, ACTION_SCHEDULED_NOTIFICATION_FIRED)
            putExtra(EXTRA_NOTIFICATION_ID, request.id)
            request.payloadData.forEach { putExtra(it.key, it.value) }
            ScheduledNotificationIntent.putNotification(this, request, androidNotificationConfiguration)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            request.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        try {
            if (canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, fireTime, pendingIntent)
            } else {
                // Without SCHEDULE_EXACT_ALARM / USE_EXACT_ALARM (API 31+) only inexact alarms are allowed.
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, fireTime, pendingIntent)
            }
            Log.d("AndroidNotificationScheduler", "Scheduled notification ${request.id} for $fireTime")
        } catch (e: SecurityException) {
            Log.e("AndroidNotificationScheduler", "Failed to schedule exact alarm", e)
            // Fallback to inexact alarm
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, fireTime, pendingIntent)
        }
    }

    private fun canScheduleExactAlarms(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

}
