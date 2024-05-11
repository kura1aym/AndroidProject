package dev.androidbroadcast.quizapp.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import dev.androidbroadcast.quizapp.domain.model.Question

class Converters {
    @TypeConverter
    fun fromArray(value: Array<Question>): String = Gson().toJson(value)

    @TypeConverter
    fun toArray(value: String): Array<Question> = Gson().fromJson(value, Array<Question>::class.java)
}
