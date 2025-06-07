package com.example.musicaptitude

data class Question(
    val id: Int,
    val type: String,
    val audioFile: String,
    val questionText: String,
    val options: List<String>,
    val followUpOptions: List<String>?,
    val correctAnswer: String,
    val correctFollowUp: String? = null
)
