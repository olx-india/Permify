package com.olx.permify.fragment

import android.Manifest
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
            handlePermissionResult(result);
        }

    private val forwardToSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            requestAgain(ArrayList(permissionRequestBuilder?.forwardPermissions))
        }

    fun forwardToSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        forwardToSettingsLauncher.launch(intent)
    }

    private fun handlePermissionResult(result: Map<String, Boolean>) {
        permissionRequestBuilder.grantedPermissions.clear()
        val showReasonList: MutableList<String> = ArrayList()
        val forwardList: MutableList<String> = ArrayList()

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

        // for reference -> https://developer.android.com/about/versions/14/changes/partial-photo-video-access

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (permissionRequestBuilder.grantedPermissions.contains(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
                if (permissionRequestBuilder.deniedPermissions.contains(Manifest.permission.READ_MEDIA_IMAGES)) {
                    permissionRequestBuilder.deniedPermissions.remove(Manifest.permission.READ_MEDIA_IMAGES)
                    showReasonList.remove(Manifest.permission.READ_MEDIA_IMAGES)
                    permissionRequestBuilder.tempReadMediaPermissions.add(Manifest.permission.READ_MEDIA_IMAGES)
                } else if (permissionRequestBuilder.permanentDeniedPermissions.contains(Manifest.permission.READ_MEDIA_IMAGES)) {
                    permissionRequestBuilder.permanentDeniedPermissions.remove(Manifest.permission.READ_MEDIA_IMAGES)
                    forwardList.remove(Manifest.permission.READ_MEDIA_IMAGES)
                    permissionRequestBuilder.tempReadMediaPermissions.add(Manifest.permission.READ_MEDIA_IMAGES)
                }
                if (permissionRequestBuilder.deniedPermissions.contains(Manifest.permission.READ_MEDIA_VIDEO)) {
                    permissionRequestBuilder.deniedPermissions.remove(Manifest.permission.READ_MEDIA_VIDEO)
                    showReasonList.remove(Manifest.permission.READ_MEDIA_VIDEO)
                    permissionRequestBuilder.tempReadMediaPermissions.add(Manifest.permission.READ_MEDIA_VIDEO)
                } else if (permissionRequestBuilder.permanentDeniedPermissions.contains(Manifest.permission.READ_MEDIA_VIDEO)) {
                    permissionRequestBuilder.permanentDeniedPermissions.remove(Manifest.permission.READ_MEDIA_VIDEO)
                    forwardList.remove(Manifest.permission.READ_MEDIA_VIDEO)
                    permissionRequestBuilder.tempReadMediaPermissions.add(Manifest.permission.READ_MEDIA_VIDEO)
                }
            }
        }

        val deniedPermissions: MutableList<String> =
            java.util.ArrayList()
        deniedPermissions.addAll(permissionRequestBuilder.deniedPermissions)
        deniedPermissions.addAll(permissionRequestBuilder.permanentDeniedPermissions)
        for (permission in deniedPermissions) {
            if (PermissionUtils.isPermissionGranted(requireContext(), permission)) {
                permissionRequestBuilder.deniedPermissions.remove(permission)
                permissionRequestBuilder.grantedPermissions.add(permission)
            }
        }
        val allGranted =
            permissionRequestBuilder.grantedPermissions.size == permissionRequestBuilder.normalPermissions.size
        if (allGranted) {
            permissionCallback?.onResult(
                deniedPermissions.isEmpty(),
                ArrayList(permissionRequestBuilder.grantedPermissions),
                deniedPermissions
            )
        } else {
            if (showReasonList.isNotEmpty()) {
                permissionRequestBuilder.showHandlePermissionDialog(
                    true,
                    ArrayList(permissionRequestBuilder.deniedPermissions)
                )
                permissionRequestBuilder.tempPermanentDeniedPermissions.addAll(forwardList)
            } else if (forwardList.isNotEmpty() || permissionRequestBuilder.tempPermanentDeniedPermissions.isNotEmpty()) {
                permissionRequestBuilder.tempPermanentDeniedPermissions.clear() // no need to store them anymore once onForwardToSettings callback.
                permissionRequestBuilder.showHandlePermissionDialog(
                    false,
                    ArrayList(permissionRequestBuilder.permanentDeniedPermissions)
                )
            }
        }
        val deniedList: MutableList<String> = java.util.ArrayList()
        deniedList.addAll(permissionRequestBuilder.deniedPermissions)
        deniedList.addAll(permissionRequestBuilder.permanentDeniedPermissions)

        permissionCallback?.onResult(
            deniedList.isEmpty(),
            ArrayList(permissionRequestBuilder.grantedPermissions),
            deniedList
        )
    }

    fun requestNow(
        permissions: List<String>,
        permissionCallback: PermissionCallback?,
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
                    .commitNow()
            }
            return fragment;
        }
    }

}