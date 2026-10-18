package com.mmk.kmpnotifier.notification

import platform.Foundation.timeIntervalSince1970
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IosNotificationSchedulingTest {

    private val now = 1_800_000_000_000L // 2027-01-15

    @Test
    fun zeroScheduledAtIsImmediate() {
        assertNull(resolveFireTimeMs(0L, now))
        assertNull(createTrigger(0L, now))
    }

    @Test
    fun pastScheduledAtIsImmediate() {
        assertNull(resolveFireTimeMs(now - 1, now))
        assertNull(createTrigger(now - 60_000L, now))
    }

    @Test
    fun smallScheduledAtIsRelativeToNow() {
        assertEquals(now + 60_000L, resolveFireTimeMs(60_000L, now))
    }

    @Test
    fun futureScheduledAtUsesCalendarTriggerAtFireDate() {
        val nowMs = currentTimeMs()
        val fireTimeMs = nowMs + 5 * 60_000L

        val trigger = assertIs<UNCalendarNotificationTrigger>(createTrigger(fireTimeMs, nowMs))

        assertFalse(trigger.repeats)
        val nextTriggerDate = assertNotNull(trigger.nextTriggerDate())
        val expectedSeconds = (fireTimeMs + 999) / 1000
        assertEquals(expectedSeconds, nextTriggerDate.timeIntervalSince1970.toLong())
    }

    @Test
    fun relativeScheduledAtUsesCalendarTrigger() {
        val nowMs = currentTimeMs()
        val trigger = assertIs<UNCalendarNotificationTrigger>(createTrigger(60_000L, nowMs))
        val nextTriggerSeconds = assertNotNull(trigger.nextTriggerDate()).timeIntervalSince1970.toLong()
        assertEquals((nowMs + 60_000L + 999) / 1000, nextTriggerSeconds)
    }

    @Test
    fun lessThanASecondAwayUsesIntervalTrigger() {
        val trigger = createTrigger(now + 200, now)
        assertIs<UNTimeIntervalNotificationTrigger>(trigger)
    }

    @Test
    fun onlyTriggeredRequestsCountAsScheduled() {
        assertFalse(null.isScheduledLocalTrigger())
        assertTrue(createTrigger(currentTimeMs() + 60_000L).isScheduledLocalTrigger())
    }
}
