package dev.androidbroadcast.quizapp.data.repository

import android.util.Log
import dev.androidbroadcast.quizapp.data.database.QuizDao
import dev.androidbroadcast.quizapp.data.mapper.mapApiResponseToQuiz
import dev.androidbroadcast.quizapp.data.model.Leaderboard
import dev.androidbroadcast.quizapp.data.model.Quiz
import dev.androidbroadcast.quizapp.data.network.QuizApiService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QuizRepository(private val quizDao: QuizDao, private val quizApiService: QuizApiService) {

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    fun insert(quizData: Quiz) {
        executorService.execute { quizDao.insert(quizData) }
    }

    fun update(quizData: Quiz) {
        executorService.execute { quizDao.update(quizData) }
    }

    fun delete(quizData: Quiz) {
        executorService.execute { quizDao.delete(quizData) }
    }

    suspend fun getQuizData(): List<Quiz> = quizDao.getQuizData()

    fun insert(leaderboardData: Leaderboard) {
        executorService.execute { quizDao.insert(leaderboardData) }
    }

    fun clearLeaderboardData() {
        executorService.execute { quizDao.clearLeaderboardData() }
    }

    suspend fun getLeaderboardData(): List<Leaderboard> = quizDao.getLeaderboardData()

    suspend fun fetchAndStoreQuizzes() {
        val response = quizApiService.fetchQuestions()
        if (response.isSuccessful) {
            val apiResponse = response.body()
            if (apiResponse != null) {
                val quiz = mapApiResponseToQuiz(apiResponse)
                quizDao.insert(quiz)
            } else {
                Log.e("QuizRepository", "Response body is null")
            }
        } else {
            Log.e("QuizRepository", "Error: ${response.errorBody()?.string()}")
        }
    }
}



