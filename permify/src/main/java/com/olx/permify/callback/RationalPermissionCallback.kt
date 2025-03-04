package com.olx.permify.callback

interface RationalPermissionCallback {
    fun onRationalPermissionCallback(temporaryPermissionDenied: List<String>)
}
