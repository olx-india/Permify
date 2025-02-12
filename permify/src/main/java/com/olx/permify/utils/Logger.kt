package com.olx.permify.utils

import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter

object Logger {
    var debug: Boolean = false

    fun v(message: String) {
        v(LOG_TAG, message)
    }

    fun w(message: String) {
        w(LOG_TAG, message)
    }

    fun d(message: String) {
        d(LOG_TAG, message)
    }

    fun e(message: String) {
        e(LOG_TAG, message)
    }

    fun i(message: String) {
        i(LOG_TAG, message)
    }

    fun v(tag: String, message: String) {
        if (debug) {
            Log.v(tag, message)
        }
    }

    fun w(tag: String, message: String) {
        if (debug) {
            Log.w(tag, message)
        }

    }

    fun w(tag: String, e: Exception) {
        if (debug) {
            Log.w(tag, getErrorString(e))
        }

    }

    fun d(tag: String, message: String) {
        if (debug) {
            Log.d(tag, message)
        }

    }

    fun i(tag: String, message: String) {
        if (debug) {
            Log.i(tag, message)
        }

    }

    fun e(tag: String, message: String) {
        if (debug) {
            Log.e(tag, message)
        }

    }

    fun e(tag: String, e: Exception) {
        if (debug) {
            Log.e(tag, getErrorString(e))
        }

    }

    fun getErrorString(e: Exception): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        e.printStackTrace(printWriter)
        return stringWriter.toString()
    }
}
