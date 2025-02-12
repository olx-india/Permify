package com.olx.permify

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.olx.permify.callback.PermissionCallback
import com.olx.permify.utils.Logger
import com.olx.permify.utils.OPEN_SETTING_MESSAGE
import com.olx.permify.utils.REQUEST_MESSAGE
import java.lang.ref.WeakReference

object Permify {

    fun requestPermission(
        activity: FragmentActivity,
        permissions: List<String>,
        permissionCallback: PermissionCallback,
        requestMessage: String = REQUEST_MESSAGE,
        openSettingMessage: String = OPEN_SETTING_MESSAGE,
        enableLogs: Boolean = false
    ) {
        Logger.debug = enableLogs
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
        requestMessage: String = REQUEST_MESSAGE,
        openSettingMessage: String = OPEN_SETTING_MESSAGE,
        enableLogs: Boolean = false
    ) {
        Logger.debug = enableLogs
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
