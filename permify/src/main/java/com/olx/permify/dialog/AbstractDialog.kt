package com.olx.permify.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View


abstract class AbstractDialog : Dialog {

    constructor(context: Context) : super(context)

    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    protected constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener)


    abstract fun getPositiveButton(): View
    abstract fun getNegativeButton(): View?
    abstract fun getPermissionList(): List<String>

}