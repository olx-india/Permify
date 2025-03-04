package com.olx.permify.callback

interface PermanentPermissionDeniedCallback {
    fun onPermanentPermissionDenied(permanentPermissionDenied: List<String>)
}
