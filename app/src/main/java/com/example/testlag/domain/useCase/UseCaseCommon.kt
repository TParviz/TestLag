package com.example.testlag.domain.useCase

import com.example.testlag.domain.repository.ApiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object UseCaseCommon {

    @Singleton
    @Provides
    fun provideGetAlertsUseCase(repository: ApiRepository) =
        GetPointsUseCase(repository)
}