package com.olx.permify

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.olx.permify.callback.PermissionCallback
import com.olx.permify.dialog.AbstractDialog
import com.olx.permify.dialog.PermissionDeniedDialog
import com.olx.permify.fragment.InvisiblePermissionFragment
import com.olx.permify.utils.LOG_TAG
import com.olx.permify.utils.Logger
import java.lang.ref.WeakReference

class PermissionRequestBuilder(
    private val fragmentActivity: WeakReference<FragmentActivity>,
    private val fragment: WeakReference<Fragment>?,
    internal val normalPermissions: List<String>
) {
    internal val grantedPermissions: MutableSet<String> = LinkedHashSet()
    internal val deniedPermissions: MutableSet<String> = LinkedHashSet()
    internal val permanentDeniedPermissions: MutableSet<String> = LinkedHashSet()
    internal val tempReadMediaPermissions: MutableSet<String> = LinkedHashSet()
    internal val tempPermanentDeniedPermissions: MutableSet<String> = LinkedHashSet()

    private var requestMessage: String? = null

    private var openSettingMessage: String? = null

    var forwardPermissions: MutableSet<String> = LinkedHashSet()

    fun setPermissionRequestMessage(requestMessage: String): PermissionRequestBuilder {
        this.requestMessage = requestMessage
        return this
    }

    fun setPermissionOpenSettingMessage(openSettingMessage: String): PermissionRequestBuilder {
        this.openSettingMessage = openSettingMessage
        return this
    }

    private val fragmentManager: FragmentManager?
        get() {
            return fragment?.get()?.childFragmentManager
                ?: fragmentActivity.get()?.supportFragmentManager
        }

    private val invisiblePermissionFragment: InvisiblePermissionFragment?
        get() {
            return fragmentManager?.let { InvisiblePermissionFragment.getInstance(it) }
        }

    fun buildAndRequest(permissionCallback: PermissionCallback) {
        validateBuilderState()
        requestPermission(permissionCallback)
    }

    private fun validateBuilderState() {
        if (requestMessage.isNullOrEmpty()) {
            Logger.e(LOG_TAG, "Permissions must be added before requesting.")
        }
    }

    fun showHandlePermissionDialog(
        showReasonOrGoSettings: Boolean,
        permissions: List<String>,
    ) {
        val message = if (showReasonOrGoSettings) openSettingMessage else requestMessage
        val context = fragmentActivity.get()
        if (context != null) {
            val defaultDialog = PermissionDeniedDialog(
                context,
                permissions,
                message ?: "",
                context.resources.getString(R.string.allow),
                context.resources.getString(R.string.cancel)
            )
            showAndHandlePermissionDialog(showReasonOrGoSettings, defaultDialog)
        }
    }

    private fun showAndHandlePermissionDialog(
        showReasonOrGoSettings: Boolean, dialog: AbstractDialog
    ) {
        val permissions = dialog.getPermissionList()
        if (permissions.isEmpty()) {
            return
        }
        dialog.show()
        val positiveButton = dialog.getPositiveButton()
        val negativeButton = dialog.getNegativeButton()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        positiveButton.isClickable = true
        positiveButton.setOnClickListener {
            dialog.dismiss()
            if (showReasonOrGoSettings) {
                invisiblePermissionFragment?.requestAgain(permissions)
            } else {
                forwardToSettings(permissions)
            }
        }
        if (negativeButton != null) {
            negativeButton.isClickable = true
            negativeButton.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun requestPermission(permissionCallback: PermissionCallback) {
        invisiblePermissionFragment?.requestNow(normalPermissions, permissionCallback, this)
    }

    private fun forwardToSettings(permissions: List<String>) {
        forwardPermissions.clear()
        forwardPermissions.addAll(permissions)
        invisiblePermissionFragment?.forwardToSettings()
    }

}
