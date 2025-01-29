package com.olx.sample

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.olx.permify.Permify
import com.olx.permify.PermissionRequestBuilder
import com.olx.permify.callback.PermissionCallback

class NormalActivity : AppCompatActivity(), PermissionCallback {

    lateinit var builder: PermissionRequestBuilder

    lateinit var tvCameraPermission: TextView
    lateinit var tvFilePermission: TextView
    lateinit var tvOpenFragment: TextView
    lateinit var frameLayout: FrameLayout

    private val listPermission = listOf<String>(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal)

        tvCameraPermission = findViewById(R.id.tv_camera_permission)
        tvFilePermission = findViewById(R.id.tv_file_permission)
        tvOpenFragment = findViewById(R.id.tv_open_fragment)
        frameLayout = findViewById(R.id.fr_open_fragment)
        tvCameraPermission.setOnClickListener {
            Permify.init(this, listOf(Manifest.permission.CAMERA))
                .setPermissionRequestMessage("OLX needs following permissions to continue")
                .setPermissionOpenSettingMessage("Please allow following permissions in settings")
                .buildAndRequest(this)
        }

        tvFilePermission.setOnClickListener {
            Permify.init(this, listPermission)
                .setPermissionRequestMessage("OLX needs following permissions to continue")
                .setPermissionOpenSettingMessage("Please allow following permissions in settings")
                .buildAndRequest(this)
        }

        tvOpenFragment.setOnClickListener {
            openFragment()
        }
    }


    private fun openFragment() {
        frameLayout.visibility = View.VISIBLE
        val fragment = PermissionFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fr_open_fragment, fragment)
            .commit()

        findViewById<LinearLayout>(R.id.ll_buttons).visibility = View.GONE
    }

    override fun onResult(
        allGranted: Boolean,
        grantedList: List<String>,
        deniedList: List<String>
    ) {
        Log.d("Permify", "All granted: $allGranted")
        Log.d("Permify", "Granted permissions: $grantedList")
        Log.d("Permify", "Denied permissions: $deniedList")
    }
}
