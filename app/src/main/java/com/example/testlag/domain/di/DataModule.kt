package com.example.testlag.domain.di

import com.example.testlag.data.api.SwaggerApi
import com.example.testlag.data.repository.ApiRepositoryImpl
import com.example.testlag.domain.repository.ApiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Singleton
    @Provides
    fun providePointsRepository(api: SwaggerApi): ApiRepository {
        return ApiRepositoryImpl(api)
    }
}