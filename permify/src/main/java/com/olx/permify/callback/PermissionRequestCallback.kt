package com.olx.permify.callback

interface PermissionRequestCallback {
    fun onResult(allGranted: Boolean, grantedList: List<String>, deniedList: List<String>)
}
