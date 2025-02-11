package com.olx.permify

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

object Permify {

    fun init(
        activity: FragmentActivity,
        permissions: List<String>
    ): PermissionRequestBuilder {
        return PermissionRequestBuilder(activity, null, permissions)
    }

    fun init(
        fragment: Fragment,
        permissions: List<String>
    ): PermissionRequestBuilder {
        return PermissionRequestBuilder(fragment.requireActivity(), fragment, permissions)
    }

}
