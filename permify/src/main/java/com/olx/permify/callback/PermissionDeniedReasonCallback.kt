package com.olx.permify.callback

import com.olx.permify.PermissionRequestBuilder

interface PermissionDeniedReasonCallback {
    fun onPermissionDeniedReasonCallback(
        permissionRequestBuilder: PermissionRequestBuilder,
        deniedPermission: List<String>
    )
}