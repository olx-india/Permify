package com.olx.permify

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.olx.permify.callback.PermissionCallback
import java.lang.ref.WeakReference

object Permify {

    fun requestPermission(
        activity: FragmentActivity,
        permissions: List<String>,
        permissionCallback: PermissionCallback,
        requestMessage: String = "OLX needs following permissions to continue",
        openSettingMessage: String = "Please allow following permissions in settings",
    ) {
        val weakActivity = WeakReference(activity)
        PermissionRequestBuilder(weakActivity, null, permissions)
            .setPermissionRequestMessages(requestMessage, openSettingMessage)
            .buildAndRequest(
                permissionCallback
            )
    }

    fun requestPermission(
        fragment: Fragment,
        permissions: List<String>,
        permissionCallback: PermissionCallback,
        requestMessage: String = "OLX needs following permissions to continue",
        openSettingMessage: String = "Please allow following permissions in settings"
    ) {
        val weakActivity = WeakReference(fragment.requireActivity())
        val weakFragment = WeakReference(fragment)
        return PermissionRequestBuilder(
            weakActivity,
            weakFragment,
            permissions
        ).setPermissionRequestMessages(requestMessage, openSettingMessage)
            .buildAndRequest(
                permissionCallback
            )
    }

}
