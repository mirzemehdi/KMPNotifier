package com.mmk.kmpnotifier.internal

import android.content.Context
import com.mmk.kmpnotifier.di.applicationContext

/**
 * Application context captured by androidx.startup (or by the
 * `KMPNotifier.initialize(context, ...)` overload).
 */
@InternalKMPNotifierApi
public fun NotifierInternals.requireApplicationContext(): Context = applicationContext
