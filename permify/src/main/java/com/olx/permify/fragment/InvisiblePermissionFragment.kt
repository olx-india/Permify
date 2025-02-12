package com.olx.permify.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.olx.permify.PermissionRequestBuilder
import com.olx.permify.PermissionUtils
import com.olx.permify.callback.PermissionCallback

class InvisiblePermissionFragment : Fragment() {

    private var permissionCallback: PermissionCallback? = null

    private lateinit var permissionRequestBuilder: PermissionRequestBuilder

    private var permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            handlePermissionResult(result)
        }

    private val forwardToSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            requestAgain(ArrayList(permissionRequestBuilder.forwardPermissions))
        }

    fun forwardToSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        forwardToSettingsLauncher.launch(intent)
    }

    private fun handlePermissionResult(result: Map<String, Boolean>) {
        permissionRequestBuilder.grantedPermissions.clear()

        val showReasonList = ArrayList<String>()
        val forwardList = ArrayList<String>()

        processPermissions(result, showReasonList, forwardList)
        handleMediaPermissions()

        val deniedPermissions = getDeniedPermissions()
        updateGrantedPermissions(deniedPermissions)

        val allGranted =
            permissionRequestBuilder.grantedPermissions.size == permissionRequestBuilder.normalPermissions.size
        if (allGranted) {
            callPermissionResultCallback(
                deniedPermissions.isEmpty(),
                permissionRequestBuilder.grantedPermissions,
                deniedPermissions
            )
        } else {
            showHandlePermissionDialogIfNeeded(showReasonList, forwardList)
        }

        val deniedList = ArrayList<String>()
        deniedList.addAll(permissionRequestBuilder.deniedPermissions)
        deniedList.addAll(permissionRequestBuilder.permanentDeniedPermissions)

        callPermissionResultCallback(
            deniedList.isEmpty(),
            permissionRequestBuilder.grantedPermissions,
            deniedList
        )
    }

    private fun callPermissionResultCallback(
        granted: Boolean,
        grantedPermissions: MutableSet<String>,
        deniedPermissions: List<String>
    ) {
        val getCaller = permissionRequestBuilder.getCallerFragmentOrActivity()
        if (isActivityOrFragmentAlive(getCaller)) {
            permissionCallback?.onResult(granted, ArrayList(grantedPermissions), deniedPermissions)
        }
    }

    private fun <T> isActivityOrFragmentAlive(caller: T?): Boolean {
        return when (caller) {
            is Fragment -> caller.isAdded && caller.isVisible
            is Activity -> !caller.isFinishing && !caller.isDestroyed
            else -> false
        }
    }

    private fun processPermissions(
        result: Map<String, Boolean>,
        showReasonList: MutableList<String>,
        forwardList: MutableList<String>
    ) {
        for ((permission, granted) in result) {
            if (granted) {
                permissionRequestBuilder.grantedPermissions.add(permission)
                permissionRequestBuilder.deniedPermissions.remove(permission)
                permissionRequestBuilder.permanentDeniedPermissions.remove(permission)
            } else {
                val shouldShowRationale = shouldShowRequestPermissionRationale(permission)
                if (shouldShowRationale) {
                    showReasonList.add(permission)
                    permissionRequestBuilder.deniedPermissions.add(permission)
                } else {
                    forwardList.add(permission)
                    permissionRequestBuilder.permanentDeniedPermissions.add(permission)
                    permissionRequestBuilder.deniedPermissions.remove(permission)
                }
            }
        }
    }

    private fun handleMediaPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (permissionRequestBuilder.grantedPermissions.contains(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
                handleMediaPermission(Manifest.permission.READ_MEDIA_IMAGES)
                handleMediaPermission(Manifest.permission.READ_MEDIA_VIDEO)
            }
        }
    }

    private fun handleMediaPermission(permission: String) {
        if (permissionRequestBuilder.deniedPermissions.contains(permission)) {
            permissionRequestBuilder.deniedPermissions.remove(permission)
            permissionRequestBuilder.tempReadMediaPermissions.add(permission)
        } else if (permissionRequestBuilder.permanentDeniedPermissions.contains(permission)) {
            permissionRequestBuilder.permanentDeniedPermissions.remove(permission)
            permissionRequestBuilder.tempReadMediaPermissions.add(permission)
        }
    }

    private fun getDeniedPermissions(): MutableList<String> {
        val deniedPermissions = ArrayList<String>()
        deniedPermissions.addAll(permissionRequestBuilder.deniedPermissions)
        deniedPermissions.addAll(permissionRequestBuilder.permanentDeniedPermissions)
        return deniedPermissions
    }

    private fun updateGrantedPermissions(deniedPermissions: MutableList<String>) {
        for (permission in deniedPermissions) {
            if (PermissionUtils.isPermissionGranted(requireContext(), permission)) {
                permissionRequestBuilder.deniedPermissions.remove(permission)
                permissionRequestBuilder.grantedPermissions.add(permission)
            }
        }
    }

    private fun showHandlePermissionDialogIfNeeded(
        showReasonList: MutableList<String>,
        forwardList: MutableList<String>
    ) {
        if (showReasonList.isNotEmpty()) {
            permissionRequestBuilder.showHandlePermissionDialog(
                true,
                ArrayList(permissionRequestBuilder.deniedPermissions)
            )
            permissionRequestBuilder.tempPermanentDeniedPermissions.addAll(forwardList)
        } else if (forwardList.isNotEmpty() || permissionRequestBuilder.tempPermanentDeniedPermissions.isNotEmpty()) {
            permissionRequestBuilder.tempPermanentDeniedPermissions.clear()
            permissionRequestBuilder.showHandlePermissionDialog(
                false,
                ArrayList(permissionRequestBuilder.permanentDeniedPermissions)
            )
        }
    }

    fun requestNow(
        permissions: List<String>,
        permissionCallback: PermissionCallback,
        permissionRequestBuilder: PermissionRequestBuilder,
    ) {
        this.permissionRequestBuilder = permissionRequestBuilder
        this.permissionCallback = permissionCallback
        permissionLauncher.launch(permissions.toTypedArray())
    }

    fun requestAgain(
        permissions: List<String>,
    ) {
        permissionLauncher.launch(permissions.toTypedArray())
    }

    companion object {

        private const val TAG = "PermissionFragment"

        fun getInstance(fragmentManager: FragmentManager): InvisiblePermissionFragment {
            var fragment = fragmentManager.findFragmentByTag(TAG) as? InvisiblePermissionFragment
            if (fragment == null) {
                fragment = InvisiblePermissionFragment()
                fragmentManager.beginTransaction().add(fragment, TAG)
                    .commitNowAllowingStateLoss()
            }
            return fragment
        }
    }

}
