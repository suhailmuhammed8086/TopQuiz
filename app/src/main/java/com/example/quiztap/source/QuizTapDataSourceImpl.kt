package com.example.quiztap.source

import com.example.quiztap.data.Difficulty
import com.example.quiztap.data.ResponseState
import com.example.quiztap.network.model.request.QuestionSetRequest
import com.example.quiztap.network.model.response.CategoriesListResponse
import com.example.quiztap.network.model.response.QuestionSetResponse
import com.example.quiztap.network.service.OpenTDBApiService
import javax.inject.Inject

class QuizTapDataSourceImpl @Inject constructor( private val quizApiService: OpenTDBApiService) :
    QuizTapDataSource {
    override suspend fun getAllCategories(): ResponseState<CategoriesListResponse> {
        val response = quizApiService.getCategories()
        return ResponseState.Success(response)
    }

    override suspend fun getQuestionSet(request: QuestionSetRequest): ResponseState<QuestionSetResponse> {
        val response = quizApiService.getQuestionSet(
            questionCount = request.questionCount,
            categoryId = if (request.categoryId != -1) {
                request.categoryId
            } else {
                null
            },
            difficulty = request.difficulty.getRequestValue(),
            answerType = request.answerType.getRequestValue()
        )
        return ResponseState.Success(response)
    }

}