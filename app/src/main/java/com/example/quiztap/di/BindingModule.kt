package com.example.quiztap.di

import com.example.quiztap.repository.QuizTapRepository
import com.example.quiztap.repository.QuizTapRepositoryImpl
import com.example.quiztap.source.QuizTapDataSource
import com.example.quiztap.source.QuizTapDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingModule {

    @Binds
    @Singleton
    abstract fun bindQuizDataSource(impl: QuizTapDataSourceImpl): QuizTapDataSource

    @Binds
    @Singleton
    abstract fun bindQuizRepository(impl: QuizTapRepositoryImpl): QuizTapRepository
}