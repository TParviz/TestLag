package com.example.testlag.domain.useCase

import com.example.testlag.domain.model.ResponsePoints
import com.example.testlag.domain.repository.ApiRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Singleton
class GetPointsUseCase @Inject constructor(
    private val repository: ApiRepository
) {
    suspend operator fun invoke(count: Int): Flow<Result<ResponsePoints>> = flow {
        try {
            emit(Result.success(repository.getPoints(count)))
        } catch (exc: Throwable) {
            emit(Result.failure(exc))
        }
    }
}