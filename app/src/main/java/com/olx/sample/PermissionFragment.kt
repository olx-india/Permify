package com.olx.sample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.olx.permify.Permify
import com.olx.permify.callback.PermissionCallback

class PermissionFragment : Fragment(), PermissionCallback {

    lateinit var permify: Permify

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_permission, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the Permify library
        // permify = Permify()

        // Find the views in the layout
        val tvCameraPermission: TextView = view.findViewById(R.id.tv_camera_permission)
        val tvFilePermission: TextView = view.findViewById(R.id.tv_file_permission)

        // Set click listeners for the TextViews
        tvCameraPermission.setOnClickListener {
            // Request CAMERA permission when clicked
//            permify.requestPermission(
//                requireActivity(),
//                Manifest.permission.CAMERA,
//                this
//            )
        }

        tvFilePermission.setOnClickListener {
            // Request WRITE_EXTERNAL_STORAGE permission when clicked
//            permify.requestPermission(
//                requireActivity(),
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                this
//            )
        }
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