package com.example.quiztap.di

import android.content.Context
import android.graphics.Path.Op
import com.example.quiztap.network.RetroInstance
import com.example.quiztap.network.service.OpenTDBApiService
import com.example.quiztap.repository.QuizTapRepository
import com.example.quiztap.repository.QuizTapRepositoryImpl
import com.example.quiztap.source.QuizTapDataSource
import com.example.quiztap.source.QuizTapDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class QuizTapModule {

    @Singleton
    @Provides
    fun provideRetroInstance(): Retrofit {
        return RetroInstance.getInstance()
    }
    @Singleton
    @Provides
    fun provideOpenTDBApiService(retrofit: Retrofit): OpenTDBApiService {
        return RetroInstance.createService(retrofit, OpenTDBApiService::class.java)
    }

}