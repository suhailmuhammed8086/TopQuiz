package com.example.quiztap.network.service

import com.example.quiztap.network.model.response.CategoriesListResponse
import com.example.quiztap.network.model.response.QuestionSetResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenTDBApiService {

    @GET("api_category.php")
    suspend fun getCategories(): CategoriesListResponse

    @GET("api.php")
    suspend fun getQuestionSet(
        @Query("amount") questionCount: Int?,
        @Query("category") categoryId: Int?,
        @Query("difficulty") difficulty: String?,
        @Query("type") answerType: String?,
        @Query("encode") encoder: String = "base64",
    ): QuestionSetResponse
}