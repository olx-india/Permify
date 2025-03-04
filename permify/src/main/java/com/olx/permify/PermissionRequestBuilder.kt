package com.olx.permify

import android.Manifest
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.olx.permify.callback.PermanentPermissionDeniedCallback
import com.olx.permify.callback.PermissionDeniedCallback
import com.olx.permify.callback.PermissionRequestCallback
import com.olx.permify.callback.RationalPermissionCallback
import com.olx.permify.dialog.AbstractDialog
import com.olx.permify.dialog.DialogCallbacks
import com.olx.permify.dialog.PermissionDeniedDialog
import com.olx.permify.fragment.InvisiblePermissionFragment
import com.olx.permify.utils.LOG_TAG
import com.olx.permify.utils.Logger
import java.lang.ref.WeakReference

class PermissionRequestBuilder(
    private val fragmentActivity: WeakReference<FragmentActivity>,
    private val fragment: WeakReference<Fragment>?,
    internal val normalPermissions: MutableList<String>
) {

    init {
        filterPermission()
    }

    internal var enablePermissionDialogs: Boolean = true
    internal val grantedPermissions: MutableSet<String> = LinkedHashSet()
    internal val deniedPermissions: MutableSet<String> = LinkedHashSet()
    internal val permanentDeniedPermissions: MutableSet<String> = LinkedHashSet()
    internal val tempReadMediaPermissions: MutableSet<String> = LinkedHashSet()
    internal val tempPermanentDeniedPermissions: MutableSet<String> = LinkedHashSet()

    var permissionDeniedCallback: PermissionDeniedCallback? = null
    var explainReasonCallbackWithBeforeParam: RationalPermissionCallback? = null
    var permanentPermissionDeniedCallback: PermanentPermissionDeniedCallback? = null

    var dialogCallbacks: DialogCallbacks? = null

    private var requestMessage: String? = null
    private var openSettingMessage: String? = null

    internal var forwardPermissions: MutableSet<String> = LinkedHashSet()

    fun displayPermissionDialogs(enablePermissionDialogs: Boolean): PermissionRequestBuilder {
        this.enablePermissionDialogs = enablePermissionDialogs
        return this
    }

    fun setDialogCallback(dialogCallbacks: DialogCallbacks?): PermissionRequestBuilder {
        this.dialogCallbacks = dialogCallbacks
        return this
    }

    fun setPermissionCallbacks(
        permissionDeniedCallback: PermissionDeniedCallback?,
        explainReasonCallbackWithBeforeParam: RationalPermissionCallback?,
        permanentPermissionDeniedCallback: PermanentPermissionDeniedCallback?
    ): PermissionRequestBuilder {
        this.permissionDeniedCallback = permissionDeniedCallback
        this.explainReasonCallbackWithBeforeParam = explainReasonCallbackWithBeforeParam
        this.permanentPermissionDeniedCallback = permanentPermissionDeniedCallback
        return this
    }

    fun setPermissionRequestMessages(
        requestMessage: String,
        openSettingMessage: String
    ): PermissionRequestBuilder {
        this.requestMessage = requestMessage
        this.openSettingMessage = openSettingMessage
        return this
    }

    fun enableDebugLogs(enableLog: Boolean) {
        Logger.debug = enableLog
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

    fun buildAndRequest(permissionRequestCallback: PermissionRequestCallback?) {
        validateBuilderState()
        requestPermission(permissionRequestCallback)
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
        val message = if (showReasonOrGoSettings) requestMessage else openSettingMessage
        val context = fragmentActivity.get()
        if (context != null) {
            val defaultDialog = PermissionDeniedDialog(
                context,
                permissions,
                message ?: "",
                context.resources.getString(R.string.allow),
                context.resources.getString(R.string.cancel)
            )
            showAndHandlePermissionDialog(
                showReasonOrGoSettings,
                defaultDialog,
                dialogCallbacks
            )
        }
    }

    private fun showAndHandlePermissionDialog(
        showReasonOrGoSettings: Boolean,
        dialog: AbstractDialog,
        dialogCallbacks: DialogCallbacks?
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
            dialogCallbacks?.onPositiveButtonClick()
            if (showReasonOrGoSettings) {
                invisiblePermissionFragment?.requestAgain(permissions)
            } else {
                forwardToSettings(permissions)
            }
        }
        if (negativeButton != null) {
            negativeButton.isClickable = true
            negativeButton.setOnClickListener {
                dialogCallbacks?.onNegativeButtonClick()
                dialog.dismiss()
            }
        }
    }

    private fun requestPermission(permissionRequestCallback: PermissionRequestCallback?) {
        invisiblePermissionFragment?.requestNow(
            normalPermissions,
            permissionRequestCallback,
            this
        )
    }

    private fun filterPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
            Manifest.permission.POST_NOTIFICATIONS in normalPermissions
        ) {
            normalPermissions.remove(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun forwardToSettings(permissions: List<String>) {
        forwardPermissions.clear()
        forwardPermissions.addAll(permissions)
        invisiblePermissionFragment?.forwardToSettings()
    }

    fun getCallerFragmentOrActivity(): Any? {
        return fragment?.get() ?: fragmentActivity.get()
    }

}
