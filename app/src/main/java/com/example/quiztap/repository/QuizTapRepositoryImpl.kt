package com.example.quiztap.repository

import com.example.quiztap.data.ResponseState
import com.example.quiztap.network.model.request.QuestionSetRequest
import com.example.quiztap.network.model.response.CategoriesListResponse
import com.example.quiztap.network.model.response.QuestionSetResponse
import com.example.quiztap.source.QuizTapDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QuizTapRepositoryImpl @Inject constructor(
    private val quizTapDataSource: QuizTapDataSource
): QuizTapRepository {
    override suspend fun getAllCategories(): ResponseState<CategoriesListResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext quizTapDataSource.getAllCategories()
        }
    }

    override suspend fun getQuestionSet(request: QuestionSetRequest): ResponseState<QuestionSetResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext quizTapDataSource.getQuestionSet(request)
        }
    }

}