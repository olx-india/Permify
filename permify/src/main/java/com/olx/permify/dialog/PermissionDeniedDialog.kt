package com.olx.permify.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.olx.permify.R
import com.olx.permify.databinding.PermifyDefaultDialogLayoutBinding

class PermissionDeniedDialog(
    context: Context,
    private val permissions: List<String>,
    private val message: String,
    private val positiveText: String,
    private val negativeText: String?,
) : AbstractDialog(context, R.style.PermifyDefaultDialog) {

    private lateinit var binding: PermifyDefaultDialogLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PermifyDefaultDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupText()
    }

    override fun getPositiveButton(): View {
        return binding.positiveBtn
    }

    override fun getNegativeButton(): View? {
        return if (negativeText.isNullOrBlank()) null else binding.negativeBtn
    }

    override fun getPermissionList(): List<String> {
        return permissions
    }

    private fun setupText() {
        binding.tvMessageText.text = message
        binding.positiveBtn.text = positiveText
        binding.negativeBtn.isVisible = !negativeText.isNullOrBlank()
        binding.negativeBtn.text = negativeText
    }

}
