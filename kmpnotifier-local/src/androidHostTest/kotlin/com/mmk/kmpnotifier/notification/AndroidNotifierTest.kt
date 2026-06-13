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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
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
}
