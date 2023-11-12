package com.mmk.kmpnotifier.permission

import android.Manifest
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.mmk.kmpnotifier.extensions.hasPermission

/**
 * in Activity
 *
 * private val permissionUtil by permissionUtil()
 *
 * then #onCreate method
 * permissionUtil.askNotificationPermission {
 *  println("HasNotification Permission: $it")
 * }
 *
 */
public fun ComponentActivity.permissionUtil(): Lazy<AndroidPermissionUtil> = lazy(LazyThreadSafetyMode.NONE) {
    AndroidPermissionUtil(this)
}

public class AndroidPermissionUtil(private val activity: ComponentActivity)  {

    private var mOnResult: ((Boolean) -> Unit)? = null

    private val requestPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            mOnResult?.invoke(isGranted)
        }

    public fun askNotificationPermission(onResult: (Boolean) -> Unit = {}) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            askIfNotHasPermission(permission = Manifest.permission.POST_NOTIFICATIONS, onResult = onResult)
        } else onResult(true)
    }


    private fun askIfNotHasPermission(permission: String, onResult: (Boolean) -> Unit = {}) {
        if (activity.hasPermission(permission)) {
            onResult(true)
        } else {
            mOnResult = onResult
            requestPermissionLauncher.launch(permission)
        }
    }


}