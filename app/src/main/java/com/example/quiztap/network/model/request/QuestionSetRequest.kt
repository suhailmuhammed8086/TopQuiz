package com.example.quiztap.network.model.request

import com.example.quiztap.data.AnswerType
import com.example.quiztap.data.Difficulty

class QuestionSetRequest {
    var questionCount: Int = 10
    var difficulty: Difficulty = Difficulty.MEDIUM
    var answerType: AnswerType = AnswerType.ALL
    var categoryId: Int = -1
}