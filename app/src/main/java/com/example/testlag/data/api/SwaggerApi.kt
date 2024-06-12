package com.example.testlag.data.api

import com.example.testlag.domain.model.ResponsePoints
import retrofit2.http.GET
import retrofit2.http.Query

interface SwaggerApi {
    @GET("/api/test/points")
    suspend fun getPoints(@Query("count") count: Int): ResponsePoints
}