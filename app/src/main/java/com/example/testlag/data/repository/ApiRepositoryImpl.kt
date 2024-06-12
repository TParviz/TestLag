package com.example.testlag.data.repository

import com.example.testlag.data.api.SwaggerApi
import com.example.testlag.domain.model.ResponsePoints
import com.example.testlag.domain.repository.ApiRepository
import javax.inject.Inject

class ApiRepositoryImpl @Inject constructor(private val api: SwaggerApi) : ApiRepository {
    override suspend fun getPoints(count: Int): ResponsePoints = api.getPoints(count)
}