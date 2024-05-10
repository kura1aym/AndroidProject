package dev.androidbroadcast.quizapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    var question: String = "default",
    var type: Int = 0,
    var answers: Array<Answer> = arrayOf(),
    var multipleCorrectAnswer: Boolean = false
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Question

        if (question != other.question) return false
        if (type != other.type) return false
        if (!answers.contentEquals(other.answers)) return false
        return multipleCorrectAnswer == other.multipleCorrectAnswer
    }

    override fun hashCode(): Int {
        var result = question.hashCode()
        result = 31 * result + type
        result = 31 * result + answers.contentHashCode()
        result = 31 * result + multipleCorrectAnswer.hashCode()
        return result
    }
}
