package com.mmk.kmpnotifier.notification

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitSecond
import platform.Foundation.NSCalendarUnitTimeZone
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNNotificationTrigger
import platform.UserNotifications.UNPushNotificationTrigger
import platform.UserNotifications.UNTimeIntervalNotificationTrigger

/** Values below this are treated as milliseconds from now rather than epoch millis (before 2001). */
private const val RELATIVE_SCHEDULE_THRESHOLD_MS = 1_000_000_000_000L

internal fun currentTimeMs(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

/** Absolute fire time for a future [scheduledAt], or null to show the notification immediately. */
internal fun resolveFireTimeMs(scheduledAt: Long, nowMs: Long): Long? {
    if (scheduledAt == 0L) return null
    val fireTimeMs = if (scheduledAt < RELATIVE_SCHEDULE_THRESHOLD_MS) nowMs + scheduledAt else scheduledAt
    return fireTimeMs.takeIf { it > nowMs }
}

/**
 * Trigger for a notification request. Null delivers immediately; scheduled notifications use a
 * [UNCalendarNotificationTrigger] for the absolute fire date (rounded up to the next whole second,
 * with the current time zone pinned), or a short interval trigger when less than a second remains.
 */
internal fun createTrigger(scheduledAt: Long, nowMs: Long = currentTimeMs()): UNNotificationTrigger? {
    val fireTimeMs = resolveFireTimeMs(scheduledAt, nowMs) ?: return null
    if (fireTimeMs - nowMs < 1000) {
        return UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)
    }

    val fireTimeSeconds = (fireTimeMs + 999) / 1000

    val fireDate = NSDate.dateWithTimeIntervalSince1970(fireTimeSeconds.toDouble())
    val units = NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or
            NSCalendarUnitHour or NSCalendarUnitMinute or NSCalendarUnitSecond or NSCalendarUnitTimeZone
    val components = NSCalendar.currentCalendar.components(units, fromDate = fireDate)
    return UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(components, repeats = false)
}

/** Local notifications the library scheduled carry a trigger; immediate ones have none. */
internal fun UNNotificationTrigger?.isScheduledLocalTrigger(): Boolean =
    this != null && this !is UNPushNotificationTrigger
