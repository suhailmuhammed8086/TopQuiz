package com.example.quiztap.utils

import android.view.View

fun View.setEnableWithAlpha(enable: Boolean) {
    isEnabled = enable
    if (enable) {
        alpha = 1f
    } else {
        alpha = 0.4f
    }
}

fun View.show() {
    visibility = View.VISIBLE
}
fun View.hide() {
    visibility = View.INVISIBLE
}
fun View.gone() {
    visibility = View.GONE
}