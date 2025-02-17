package com.olx.permify.callback

import com.olx.permify.utils.Logger

class DefaultPermissionCallbackImpl : PermissionCallback {
    override fun onResult(
        allGranted: Boolean,
        grantedList: List<String>,
        deniedList: List<String>
    ) {
        if (allGranted) {
            Logger.d("All permissions granted: $grantedList")
        } else {
            Logger.d("Permissions granted: $grantedList")
            Logger.d("Permissions denied: $deniedList")
        }
    }
}