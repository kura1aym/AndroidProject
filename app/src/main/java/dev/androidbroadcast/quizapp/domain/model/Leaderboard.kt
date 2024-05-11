package dev.androidbroadcast.quizapp.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Leaderboard(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "player")
    val player: String = "Player",

    @ColumnInfo(name = "topic")
    val topic: String = "default",

    @ColumnInfo(name = "score")
    val score: Int = 0
)
