package com.olx.permify

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.olx.permify.callback.PermissionCallback
import com.olx.permify.dialog.AbstractDialog
import com.olx.permify.dialog.PermissionDeniedDialog
import com.olx.permify.fragment.InvisiblePermissionFragment

class PermissionRequestBuilder(
    private val fragmentActivity: FragmentActivity,
    private val fragment: Fragment?,
    val normalPermissions: List<String>
) {
    var grantedPermissions: MutableSet<String> = LinkedHashSet()
    var deniedPermissions: MutableSet<String> = LinkedHashSet()
    var permanentDeniedPermissions: MutableSet<String> = LinkedHashSet()
    var tempReadMediaPermissions: MutableSet<String> = LinkedHashSet()
    var tempPermanentDeniedPermissions: MutableSet<String> = LinkedHashSet()

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

    private val fragmentManager: FragmentManager
        get() {
            return fragment?.childFragmentManager ?: fragmentActivity.supportFragmentManager
        }

    private val invisiblePermissionFragment: InvisiblePermissionFragment
        get() {
            return InvisiblePermissionFragment.getInstance(fragmentManager)
        }

    fun buildAndRequest(permissionCallback: PermissionCallback) {
        validateBuilderState()
        requestPermission(permissionCallback)
    }

    private fun validateBuilderState() {
        if (requestMessage.isNullOrEmpty()) {
            throw IllegalStateException("Permissions must be added before requesting.")
        }
    }

    fun showHandlePermissionDialog(
        showReasonOrGoSettings: Boolean,
        permissions: List<String>,
    ) {
        val message = if (showReasonOrGoSettings) openSettingMessage else requestMessage
        val defaultDialog = PermissionDeniedDialog(
            fragmentActivity,
            permissions,
            message ?: "",
            "Allow",
            "Cancel"
        )
        showAndHandlePermissionDialog(showReasonOrGoSettings, defaultDialog)
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
                invisiblePermissionFragment.requestAgain(permissions)
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
        invisiblePermissionFragment.requestNow(normalPermissions, permissionCallback, this)
    }

    private fun forwardToSettings(permissions: List<String>) {
        forwardPermissions.clear()
        forwardPermissions.addAll(permissions)
        invisiblePermissionFragment.forwardToSettings()
    }

}
