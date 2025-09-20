package com.example.quiztap.model

import android.os.Parcelable
import com.example.quiztap.data.AnswerType
import com.example.quiztap.data.Difficulty
import com.example.quiztap.data.TimeSetting
import com.example.quiztap.network.model.response.CategoryModel
import com.example.quiztap.network.model.response.QuestionSet
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionDataSetModel(
    val questions : List<QuestionSet>,
    val category: CategoryModel? = null,
    val questionCount: Int,
    val answerType: AnswerType,
    val difficulty: Difficulty,
    val timeSetting: TimeSetting,
    val timeInSec: Int = 0
): Parcelable
