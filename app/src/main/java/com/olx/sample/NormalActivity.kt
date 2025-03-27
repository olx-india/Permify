package com.olx.sample

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.olx.permify.Permify
import com.olx.permify.callback.PermanentPermissionDeniedCallback
import com.olx.permify.callback.PermissionDeniedCallback
import com.olx.permify.callback.PermissionRequestCallback
import com.olx.permify.callback.RationalPermissionCallback
import com.olx.sample.databinding.ActivityNormalBinding

class NormalActivity : AppCompatActivity(), PermissionRequestCallback {

    private lateinit var binding: ActivityNormalBinding

    private val listPermission = listOf<String>(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNormalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvContactPermission.setOnClickListener {
            Permify.requestPermission(
                activity = this,
                permissions = listOf(Manifest.permission.READ_PHONE_STATE),

                permissionRequestCallback = object : PermissionRequestCallback {
                    override fun onResult(
                        allGranted: Boolean,
                        grantedList: List<String>,
                        deniedList: List<String>
                    ) {
                        Log.e(
                            "Permify",
                            "READ_PHONE_STATE permission status:" +
                                    " ${if (allGranted) "Granted" else "Denied"}"
                        )
                    }
                }
            )
        }

        binding.tvPostNotificationPermission.setOnClickListener {
            Permify.requestPermission(
                activity = this,
                permissions = listOf(Manifest.permission.POST_NOTIFICATIONS),
                permissionDeniedCallback = object : PermissionDeniedCallback {
                    override fun onPermissionDenied(permissionDeniedList: List<String>) {
                        Log.e("Permify  ", "PermissionDeniedCallback")
                    }
                },
//                rationalPermissionCallback = object : RationalPermissionCallback {
//                    override fun onRationalPermissionCallback(temporaryPermissionDenied: List<String>) {
//                        Log.e("Permify  ", "RationalPermissionCallback")
//                    }
//                },
//                permanentPermissionDeniedCallback = object : PermanentPermissionDeniedCallback {
//                    override fun onPermanentPermissionDenied(permanentPermissionDenied: List<String>) {
//                        Log.e("Permify  ", "ForwardToSettingsCallback")
//                    }
//                },
                permissionRequestCallback = object : PermissionRequestCallback {
                    override fun onResult(
                        allGranted: Boolean,
                        grantedList: List<String>,
                        deniedList: List<String>
                    ) {
                        Log.e(
                            "Permify",
                            "POST_NOTIFICATIONS permission status:" +
                                    " ${if (allGranted) "Granted" else "Denied"}"
                        )
                    }
                }
            )
        }

        binding.tvLocationPermission.setOnClickListener {
            Permify.requestPermission(
                activity = this,
                permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                requestMessage = "OLX needs following permissions to continue",
                openSettingMessage = "Please allow following permissions in settings",
                permissionDeniedCallback = object : PermissionDeniedCallback {
                    override fun onPermissionDenied(permissionDeniedList: List<String>) {
                        Log.e("Permify  ", "PermissionDeniedCallback")
                    }
                },
                rationalPermissionCallback = object : RationalPermissionCallback {
                    override fun onRationalPermissionCallback(temporaryPermissionDenied: List<String>) {
                        Log.e("Permify  ", "RationalPermissionCallback")
                    }
                },
                permanentPermissionDeniedCallback = object : PermanentPermissionDeniedCallback {
                    override fun onPermanentPermissionDenied(permanentPermissionDenied: List<String>) {
                        Log.e("Permify  ", "ForwardToSettingsCallback")
                    }
                },
                permissionRequestCallback = object : PermissionRequestCallback {
                    override fun onResult(
                        allGranted: Boolean,
                        grantedList: List<String>,
                        deniedList: List<String>
                    ) {
                        Log.e(
                            "Permify",
                            "OLX PERMISSION" +
                                    " ${if (allGranted) "Granted" else "Denied"}"
                        )
                        Log.e(
                            "Permify",
                            grantedList.toString()
                        )
                        Log.e(
                            "Permify",
                            deniedList.toString()
                        )
                    }
                }
            )
        }

        binding.tvOpenFragment.setOnClickListener {
            openFragment(binding)
        }
    }

    private fun openFragment(binding: ActivityNormalBinding) {
        binding.frOpenFragment.visibility = View.VISIBLE
        val fragment = PermissionRequestFragment()

        supportFragmentManager.beginTransaction().replace(R.id.fr_open_fragment, fragment).commit()

        findViewById<LinearLayout>(R.id.ll_buttons).visibility = View.GONE
    }

    override fun onResult(
        allGranted: Boolean, grantedList: List<String>, deniedList: List<String>
    ) {
        Log.d("Permify", "All granted: $allGranted")
        Log.d("Permify", "Granted permissions: $grantedList")
        Log.d("Permify", "Denied permissions: $deniedList")
    }
}