package com.olx.permify.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionResultReconcilerTest {

    @Test
    fun `empty result leaves requested camera unclassified so it can be denied`() {
        val missing = PermissionResultReconciler.permissionsMissingFromResult(
            requested = listOf("android.permission.CAMERA"),
            granted = emptySet(),
            temporaryDenied = emptySet(),
            permanentDenied = emptySet()
        )

        assertEquals(listOf("android.permission.CAMERA"), missing)
    }

    @Test
    fun `processed denials are not treated as missing`() {
        val missing = PermissionResultReconciler.permissionsMissingFromResult(
            requested = listOf("android.permission.CAMERA"),
            granted = emptySet(),
            temporaryDenied = setOf("android.permission.CAMERA"),
            permanentDenied = emptySet()
        )

        assertTrue(missing.isEmpty())
    }

    @Test
    fun `granted result that AppOps revoked is flagged`() {
        val revoked = PermissionResultReconciler.grantedPermissionsThatAreRevoked(
            granted = setOf("android.permission.CAMERA")
        ) { false }

        assertEquals(listOf("android.permission.CAMERA"), revoked)
    }

    @Test
    fun `actually granted permission is not flagged as revoked`() {
        val revoked = PermissionResultReconciler.grantedPermissionsThatAreRevoked(
            granted = setOf("android.permission.CAMERA")
        ) { true }

        assertTrue(revoked.isEmpty())
    }

    @Test
    fun `partial media grant does not mark selected permission as missing`() {
        val missing = PermissionResultReconciler.permissionsMissingFromResult(
            requested = listOf(
                "android.permission.READ_MEDIA_IMAGES",
                "android.permission.READ_MEDIA_VIDEO",
                "android.permission.READ_MEDIA_VISUAL_USER_SELECTED"
            ),
            granted = setOf("android.permission.READ_MEDIA_VISUAL_USER_SELECTED"),
            temporaryDenied = setOf(
                "android.permission.READ_MEDIA_IMAGES",
                "android.permission.READ_MEDIA_VIDEO"
            ),
            permanentDenied = emptySet()
        )

        assertTrue(missing.isEmpty())
    }
}
