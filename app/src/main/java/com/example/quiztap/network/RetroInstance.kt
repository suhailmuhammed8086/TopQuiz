package com.example.quiztap.network

import android.content.Context
import com.example.quiztap.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetroInstance {
    // TODO: Move to gradle
    private const val BASE_URL = "https://opentdb.com/"

    fun getInstance() : Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)


        val client = OkHttpClient().newBuilder()
            .apply { if (BuildConfig.DEBUG) { addInterceptor(loggingInterceptor) } }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun <T> createService(retrofit: Retrofit, service: Class<T>): T {
        return retrofit.create(service)
    }

}