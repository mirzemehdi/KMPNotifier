package com.mmk.kmpnotifier.notification

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowAlarmManager
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AndroidNotifierTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val configuration = NotificationPlatformConfiguration.Android(
        notificationIconResId = android.R.drawable.ic_dialog_info,
    )

    private val notificationManager: NotificationManager
        get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val alarmManager: AlarmManager
        get() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private lateinit var notifier: AndroidNotifier

    @Before
    fun setUp() {
        registerLauncherActivity()
        notifier = AndroidNotifier(
            context = context,
            androidNotificationConfiguration = configuration,
            notificationChannelFactory = NotificationChannelFactory(
                context = context,
                channelData = configuration.notificationChannelData,
            ),
            permissionUtil = object : PermissionUtil {
                override fun hasNotificationPermission(onPermissionResult: (Boolean) -> Unit) =
                    onPermissionResult(true)

                override fun askNotificationPermission(onPermissionResult: (Boolean) -> Unit) =
                    onPermissionResult(true)
            },
        )
    }

    @After
    fun tearDown() = ShadowAlarmManager.reset()

    private fun registerLauncherActivity() {
        val componentName = ComponentName(context.packageName, "com.mmk.kmpnotifier.TestLauncherActivity")
        val shadowPackageManager = shadowOf(context.packageManager)
        shadowPackageManager.addActivityIfNotPresent(componentName)
        shadowPackageManager.addIntentFilterForActivity(
            componentName,
            IntentFilter(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) },
        )
    }

    private fun idleMainLooper() = shadowOf(Looper.getMainLooper()).idle()

    private fun postedNotifications(): List<Notification> =
        shadowOf(notificationManager).allNotifications

    @Test
    fun notifyPostsNotificationWithTitleBodyAndChannel() {
        notifier.notify(id = 10, title = "Hello", body = "World")
        idleMainLooper()

        val notifications = postedNotifications()
        assertEquals(1, notifications.size)
        val notification = notifications.single()
        assertEquals("Hello", notification.extras.getString(Notification.EXTRA_TITLE))
        assertEquals("World", notification.extras.getString(Notification.EXTRA_TEXT))
        assertEquals(configuration.notificationChannelData.id, notification.channelId)
    }

    @Test
    fun notifyWithoutIdReturnsGeneratedNonNegativeId() {
        val id = notifier.notify(title = "Hello", body = "World")
        idleMainLooper()
        assertTrue(id >= 0)
        assertEquals(1, postedNotifications().size)
    }

    @Test
    fun notifyAddsActionButtons() {
        notifier.notify {
            id = 11
            title = "With actions"
            body = "Body"
            actions = listOf(
                NotificationAction(id = "reply", title = "Reply", allowsTextInput = true),
                NotificationAction(id = "dismiss", title = "Dismiss"),
            )
        }
        idleMainLooper()

        val notification = postedNotifications().single()
        assertEquals(2, notification.actions.size)
        assertEquals("Reply", notification.actions[0].title)
        assertEquals("Dismiss", notification.actions[1].title)
    }

    @Test
    fun removeCancelsNotification() {
        notifier.notify(id = 12, title = "T", body = "B")
        idleMainLooper()
        assertEquals(1, postedNotifications().size)

        notifier.remove(12)
        idleMainLooper()
        assertEquals(0, postedNotifications().size)
    }

    @Test
    fun removeAllCancelsAllNotifications() {
        notifier.notify(id = 13, title = "T1", body = "B1")
        notifier.notify(id = 14, title = "T2", body = "B2")
        idleMainLooper()
        assertEquals(2, postedNotifications().size)

        notifier.removeAll()
        idleMainLooper()
        assertEquals(0, postedNotifications().size)
    }

    @Test
    fun futureScheduledAtSchedulesAlarmInsteadOfPosting() {
        notifier.notify {
            id = 15
            title = "Scheduled"
            body = "Later"
            scheduledAt = System.currentTimeMillis() + 60_000L
        }
        idleMainLooper()

        assertEquals(0, postedNotifications().size)
        assertEquals(1, shadowOf(alarmManager).scheduledAlarms.size)
    }

    private fun scheduleInOneMinute(block: NotifierBuilder.() -> Unit = {}) {
        notifier.notify {
            id = 20
            title = "Two gentle minutes?"
            body = "Your plant is waiting."
            payloadData = mapOf("type" to "reminder")
            scheduledAt = System.currentTimeMillis() + 60_000L
            block()
        }
    }

    private fun fireScheduledAlarm() {
        val alarm = shadowOf(alarmManager).scheduledAlarms.single()
        val intent = shadowOf(alarm.operation).savedIntent
        NotificationReceiver().onReceive(context, intent)
        idleMainLooper()
    }

    @Test
    fun scheduledAlarmPostsNotificationWhenFired() {
        scheduleInOneMinute {
            actions = listOf(NotificationAction(id = "snooze", title = "Snooze"))
        }
        idleMainLooper()

        fireScheduledAlarm()

        val notification = postedNotifications().single()
        assertEquals("Two gentle minutes?", notification.extras.getString(Notification.EXTRA_TITLE))
        assertEquals("Your plant is waiting.", notification.extras.getString(Notification.EXTRA_TEXT))
        assertEquals(configuration.notificationChannelData.id, notification.channelId)
        assertEquals("Snooze", notification.actions.single().title)
    }

    @Test
    fun scheduledNotificationClickCarriesPayload() {
        scheduleInOneMinute()
        idleMainLooper()

        fireScheduledAlarm()

        val clickIntent = shadowOf(postedNotifications().single().contentIntent).savedIntent
        assertEquals("reminder", clickIntent.getStringExtra("type"))
    }

    @Test
    fun relativeScheduledAtSchedulesAlarmInTheFuture() {
        val before = System.currentTimeMillis()
        notifier.notify {
            id = 21
            title = "Relative"
            scheduledAt = 60_000L
        }

        val alarm = shadowOf(alarmManager).scheduledAlarms.single()
        assertTrue(alarm.triggerAtMs >= before + 60_000L)
    }

    @Test
    fun removeCancelsScheduledAlarm() {
        scheduleInOneMinute()
        assertEquals(1, shadowOf(alarmManager).scheduledAlarms.size)

        notifier.remove(20)

        assertEquals(0, shadowOf(alarmManager).scheduledAlarms.size)
    }

    @Test
    fun usesExactAlarmWhenAllowed() {
        ShadowAlarmManager.setCanScheduleExactAlarms(true)
        scheduleInOneMinute()

        val alarm = shadowOf(alarmManager).scheduledAlarms.single()
        assertEquals(ShadowAlarmManager.WINDOW_EXACT, alarm.windowLengthMs)
        assertTrue(alarm.isAllowWhileIdle)
    }

    @Test
    fun fallsBackToInexactAlarmWithoutExactAlarmPermission() {
        ShadowAlarmManager.setCanScheduleExactAlarms(false)
        scheduleInOneMinute()

        val alarm = shadowOf(alarmManager).scheduledAlarms.single()
        assertTrue(alarm.windowLengthMs != ShadowAlarmManager.WINDOW_EXACT)
        assertTrue(alarm.isAllowWhileIdle)
    }
}
