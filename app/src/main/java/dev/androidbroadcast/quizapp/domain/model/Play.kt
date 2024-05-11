package dev.androidbroadcast.quizapp.domain.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Play(
    val img: Uri? = null,
    val topic: String = "default",
    val questions: Array<Question> = arrayOf(),
    var questionStatus: Array<SidebarData> = arrayOf(),
    var skippedQuestion: IntArray = intArrayOf(),
    var timeForSkippedQuestion: Boolean = false,
    var currentIndex: Int = 0,
    var correctAnswer: Float = 0F
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Play

        if (img != other.img) return false
        if (!questions.contentEquals(other.questions)) return false
        if (!skippedQuestion.contentEquals(other.skippedQuestion)) return false
        if (timeForSkippedQuestion != other.timeForSkippedQuestion) return false
        if (currentIndex != other.currentIndex) return false
        return correctAnswer == other.correctAnswer
    }

    override fun hashCode(): Int {
        var result = img?.hashCode() ?: 0
        result = 31 * result + questions.contentHashCode()
        result = 31 * result + skippedQuestion.contentHashCode()
        result = 31 * result + timeForSkippedQuestion.hashCode()
        result = 31 * result + currentIndex
        result = 31 * result + correctAnswer.hashCode()
        return result
    }
}
