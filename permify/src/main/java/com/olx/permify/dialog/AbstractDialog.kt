package com.olx.permify.dialog

import android.app.Dialog
import android.content.Context
import android.view.View

abstract class AbstractDialog(context: Context, themeResId: Int) :
    Dialog(context, themeResId) {

    abstract fun getPositiveButton(): View

    abstract fun getNegativeButton(): View?

    abstract fun getPermissionList(): List<String>

}
