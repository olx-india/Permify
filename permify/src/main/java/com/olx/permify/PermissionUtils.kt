package com.olx.permify

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isNotificationEnable(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun areAllPermissionsGranted(activity: Activity, permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun getDeniedPermissions(activity: Activity, permissions: Array<String>): Array<String> {
        return permissions.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
    }

    fun getPermissionsNeedingRationale(
        activity: Activity, permissions: Array<String>
    ): Array<String> {
        return permissions.filter {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }.toTypedArray()
    }

}
