package com.example.musicaptitude

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnswerResult(
    val question: String,
    val userAnswer: String,
    val correctAnswer: String
) : Parcelable


