package com.olx.permify

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference

object Permify {

    fun init(
        activity: FragmentActivity,
        permissions: List<String>
    ): PermissionRequestBuilder {
        val weakActivity = WeakReference(activity)
        return PermissionRequestBuilder(weakActivity, null, permissions)
    }

    fun init(
        fragment: Fragment,
        permissions: List<String>
    ): PermissionRequestBuilder {
        val weakActivity = WeakReference(fragment.requireActivity())
        val weakFragment = WeakReference(fragment)
        return PermissionRequestBuilder(weakActivity, weakFragment, permissions)
    }

}
