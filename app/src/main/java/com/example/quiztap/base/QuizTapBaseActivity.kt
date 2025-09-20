package com.example.quiztap.base

import androidx.appcompat.app.AppCompatActivity

open class QuizTapBaseActivity: AppCompatActivity() {


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