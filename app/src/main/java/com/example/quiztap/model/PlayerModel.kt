package com.example.quiztap.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerModel (
    val userName: String,
    val answers: MutableMap<Int, String> = mutableMapOf(),
    var totalTimeTaken: Int = 0
): Parcelable {
    @IgnoredOnParcel
    var mark = 0
}