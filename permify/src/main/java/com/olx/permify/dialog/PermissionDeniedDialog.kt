package com.olx.permify.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.olx.permify.R

class PermissionDeniedDialog(
    context: Context,
    private val permissions: List<String>,
    private val message: String,
    private val positiveText: String,
    private val negativeText: String?,
) :
    AbstractDialog(context, R.style.PermifyDefaultDialog) {

    private lateinit var permissionsLayout: LinearLayout
    private lateinit var messageText: TextView
    private lateinit var positiveBtn: Button
    private lateinit var negativeBtn: Button
    private lateinit var permissionIcon: ImageView
    private lateinit var permissionText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.permify_default_dialog_layout)

        permissionsLayout = findViewById(R.id.permissionsLayout)
        messageText = findViewById(R.id.messageText)
        positiveBtn = findViewById(R.id.positiveBtn)
        negativeBtn = findViewById(R.id.negativeBtn)

        setupText()
        setupWindow()
    }

    override fun getPositiveButton(): View {
        return positiveBtn
    }

    override fun getNegativeButton(): View? {
        if (negativeText != null) return negativeBtn
        else return null
    }

    override fun getPermissionList(): List<String> {
        return permissions
    }

    private fun setupText() {
        messageText.text = message
        positiveBtn.text = positiveText
        if (negativeText != null) {
            negativeBtn.visibility = View.VISIBLE
            negativeBtn.text = negativeText
        } else {
            negativeBtn.visibility = View.GONE
        }

    }


    private fun setupWindow() {
        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels
        window?.let {
            val param = it.attributes
            it.setGravity(Gravity.CENTER)
            if (width < height) {
                param.width = (width * 0.86).toInt()
            } else {
                param.width = (width * 0.6).toInt()
            }
            it.attributes = param
        }
    }
}