package com.example.testlag.domain.model

data class ResponsePoints(
    val points: List<Points>
)

data class Points(
    val x: Float,
    val y: Float
)