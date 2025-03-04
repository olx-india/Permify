package com.olx.permify.callback

interface PermissionDeniedCallback {
    fun onPermissionDenied(permissionDeniedList: List<String>)
}
