package com.example.quiztap.utils

import android.content.Intent
import android.os.Build
import android.os.Parcelable
import android.util.Base64
import android.util.Log


object Utils {
    fun getFormattedSeconds(secondsInput: Int) : String{
        val minutes = secondsInput / 60
        val seconds = secondsInput % 60
        var result = ""
        if (minutes > 0) {
            result += "$minutes m "
        }
        if (seconds > 0) {
            result += "$seconds s"
        }
        return result
    }
}

fun String.decodeBase64() : String {
    val decodedBytes = Base64.decode(this, Base64.DEFAULT)
    return String(decodedBytes).trim()
}

fun <T> T.log(key: String = ""): T {
    Log.e("Quick", "$key: $this", )
    return this
}


inline fun <reified T: Parcelable>Intent.getParcelableExtraCompact(name: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(name, clazz)
    } else {
        getParcelableExtra(name) as T?
    }
}

inline fun <reified T: Parcelable>Intent.getParcelableListExtraCompact(name: String, clazz: Class<T>): ArrayList<T>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableArrayListExtra(name, clazz)
    } else {
        getParcelableArrayListExtra(name)
    }
}