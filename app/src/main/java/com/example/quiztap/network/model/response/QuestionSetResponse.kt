package com.example.quiztap.network.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

class QuestionSetResponse(
    @SerializedName("response_code")
    val responseCode: Int,
    val results: List<QuestionSet>
)

@Parcelize
data class QuestionSet(
    var id: Int = -1,
    val type: String,
    val difficulty: String,
    val category: String,
    val question: String,
    @SerializedName("correct_answer")
    val correctAnswer: String,
    @SerializedName("incorrect_answers")
    val incorrectAnswers: List<String>
): Parcelable {
    @IgnoredOnParcel
    var questionIndex: Int = 0
    @IgnoredOnParcel
    var userAnswer: String? = null
}