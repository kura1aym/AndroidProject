package dev.androidbroadcast.quizapp.domain.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Quiz(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "title")
    var title: String = "default",

    @ColumnInfo(name = "desc")
    var desc: String = "default",

    @ColumnInfo(name = "img")
    var img: String? = null,

    @ColumnInfo(name = "color")
    var color: Int? = null,

    @ColumnInfo(name = "questions")
    var questions: Array<Question> = arrayOf()
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Quiz

        if (id != other.id) return false
        if (title != other.title) return false
        if (desc != other.desc) return false
        if (img != other.img) return false
        return questions.contentEquals(other.questions)
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + desc.hashCode()
        result = 31 * result + (img?.hashCode() ?: 0)
        result = 31 * result + questions.contentHashCode()
        return result
    }
}
