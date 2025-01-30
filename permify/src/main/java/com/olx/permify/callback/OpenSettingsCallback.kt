package com.olx.permify.callback

import com.olx.permify.PermissionRequestBuilder

interface OpenSettingsCallback {
    fun onOpenSettingsCallback(
        permissionRequestBuilder: PermissionRequestBuilder,
        deniedPermission: List<String>
    )
}