package dev.androidbroadcast.quizapp.domain.repository

import dev.androidbroadcast.quizapp.domain.model.Leaderboard
import dev.androidbroadcast.quizapp.domain.model.Quiz

interface QuizRepository {
    fun insert(quizData: Quiz)
    fun update(quizData: Quiz)
    fun delete(quizData: Quiz)
    suspend fun getQuizData(): List<Quiz>
    fun insert(leaderboardData: Leaderboard)
    fun clearLeaderboardData()
    suspend fun getLeaderboardData(): List<Leaderboard>
    suspend fun fetchAndStoreQuizzes()
}


