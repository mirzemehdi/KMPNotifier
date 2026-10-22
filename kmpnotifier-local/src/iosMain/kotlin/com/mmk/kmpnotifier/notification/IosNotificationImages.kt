package com.mmk.kmpnotifier.notification

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.cinterop.reinterpret
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.writeToURL
import platform.UserNotifications.UNNotificationAttachment
import kotlin.random.Random

/** Image types UNNotificationAttachment accepts; the type is inferred from the file extension. */
private val SUPPORTED_IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "gif")

/**
 * File extension for image [header] bytes: sniffed from the magic number, else [hint]
 * (e.g. the URL's path extension) when it is a supported type, else "jpg".
 */
internal fun imageFileExtension(header: ByteArray, hint: String?): String {
    fun startsWith(vararg bytes: Int) =
        header.size >= bytes.size && bytes.indices.all { header[it] == bytes[it].toByte() }

    return when {
        startsWith(0x89, 0x50, 0x4E, 0x47) -> "png"
        startsWith(0xFF, 0xD8, 0xFF) -> "jpg"
        startsWith(0x47, 0x49, 0x46, 0x38) -> "gif"
        else -> hint?.lowercase()?.takeIf { it in SUPPORTED_IMAGE_EXTENSIONS } ?: "jpg"
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.headerBytes(count: Int = 8): ByteArray {
    val size = minOf(count, length.toInt())
    val pointer = bytes?.reinterpret<ByteVar>() ?: return ByteArray(0)
    return ByteArray(size) { pointer[it] }
}

/**
 * Writes [data] to a fresh temp file and attaches it. The system moves attachment files into its
 * own store, so attaching the caller's file directly would delete it — always attach a copy.
 */
@OptIn(ExperimentalForeignApi::class)
internal fun createImageAttachment(data: NSData, extensionHint: String?): UNNotificationAttachment? {
    val extension = imageFileExtension(data.headerBytes(), extensionHint)
    val tempFileUrl = NSURL.fileURLWithPath(
        NSTemporaryDirectory() + "/notification_image_${Random.nextInt(0, Int.MAX_VALUE)}.$extension"
    )
    if (!data.writeToURL(tempFileUrl, true)) return null
    return UNNotificationAttachment.attachmentWithIdentifier("notification_image", tempFileUrl, null, null)
}
