package dev.androidbroadcast.quizapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Answer(
    var answer: String = "default",
    var isCorrect: Boolean = false
) : Parcelable
