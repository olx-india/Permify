package com.olx.sample

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.olx.permify.Permify
import com.olx.permify.callback.PermissionRequestCallback
import com.olx.sample.databinding.FragmentPermissionBinding

class PermissionRequestFragment : Fragment(), PermissionRequestCallback {

    private lateinit var binding: FragmentPermissionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvReadPhoneStatePermission.setOnClickListener {
            Permify.requestPermission(
                fragment = this,
                permissionRequestCallback = this,
                permissions = listOf(Manifest.permission.READ_PHONE_STATE),
                requestMessage = "OLX needs following permissions to continue",
                openSettingMessage = "Please allow following permissions in settings"
            )
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