package com.example.quiztap.repository

import com.example.quiztap.data.ResponseState
import com.example.quiztap.network.model.request.QuestionSetRequest
import com.example.quiztap.network.model.response.CategoriesListResponse
import com.example.quiztap.network.model.response.QuestionSetResponse

interface QuizTapRepository {
    suspend fun getAllCategories(): ResponseState<CategoriesListResponse>

    suspend fun getQuestionSet(request: QuestionSetRequest): ResponseState<QuestionSetResponse>
}