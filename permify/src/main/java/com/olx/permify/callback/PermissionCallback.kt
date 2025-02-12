package com.olx.permify.callback

interface PermissionCallback {
    fun onResult(allGranted: Boolean, grantedList: List<String>, deniedList: List<String>)
}
