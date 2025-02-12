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
        setupWindow()
    }

    override fun getPositiveButton(): View {
        return binding.positiveBtn
    }

    override fun getNegativeButton(): View? {
        return if (negativeText != null) binding.negativeBtn else null
    }

    override fun getPermissionList(): List<String> {
        return permissions
    }

    private fun setupText() {
        binding.messageText.text = message
        binding.positiveBtn.text = positiveText
        binding.negativeBtn.isVisible = !negativeText.isNullOrBlank()
        binding.negativeBtn.text = negativeText
    }

    private fun setupWindow() {
        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels
        window?.let {
            val param = it.attributes
            param.width = if (width < height) {
                (width * 0.86).toInt()
            } else {
                (width * 0.6).toInt()
            }
            it.attributes = param
        }
    }
}
