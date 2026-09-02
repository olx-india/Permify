package com.olx.permify.utils

/**
 * Reconciles the OS permission result with the real grant state.
 *
 * [ActivityResultContracts.RequestMultiplePermissions] can return an empty map
 * (cancel / some OEM revoked-camera paths) or `true` while AppOps still has the
 * permission revoked. Treating that as success launches CAMERA intents and
 * crashes with SecurityException on Samsung.
 */
internal object PermissionResultReconciler {

    fun grantedPermissionsThatAreRevoked(
        granted: Set<String>,
        isActuallyGranted: (String) -> Boolean
    ): List<String> = granted.filterNot(isActuallyGranted)

    fun permissionsMissingFromResult(
        requested: Collection<String>,
        granted: Set<String>,
        temporaryDenied: Set<String>,
        permanentDenied: Set<String>
    ): List<String> = requested.filter { permission ->
        permission !in granted &&
            permission !in temporaryDenied &&
            permission !in permanentDenied
    }
}
