package com.example.testlag.domain.repository

import com.example.testlag.domain.model.ResponsePoints

interface ApiRepository {
    suspend fun getPoints(count: Int): ResponsePoints
}