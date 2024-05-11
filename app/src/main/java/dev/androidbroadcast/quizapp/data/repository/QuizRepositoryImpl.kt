package dev.androidbroadcast.quizapp.data.repository

import android.util.Log
import dev.androidbroadcast.quizapp.data.database.QuizDao
import dev.androidbroadcast.quizapp.data.mapper.mapApiResponseToQuiz
import dev.androidbroadcast.quizapp.data.network.QuizApiService
import dev.androidbroadcast.quizapp.domain.model.Leaderboard
import dev.androidbroadcast.quizapp.domain.model.Quiz
import dev.androidbroadcast.quizapp.domain.repository.QuizRepository
import kotlinx.coroutines.Dispatchers

class QuizRepositoryImpl(private val quizDao: QuizDao, private val quizApiService: QuizApiService) : QuizRepository {

    override fun insert(quizData: Quiz) {
        quizDao.insert(quizData)
    }

    override fun update(quizData: Quiz) {
        quizDao.update(quizData)
    }

    override fun delete(quizData: Quiz) {
        quizDao.delete(quizData)
    }

    override suspend fun getQuizData(): List<Quiz> {
        return quizDao.getQuizData()
    }

    override fun insert(leaderboardData: Leaderboard) {
        quizDao.insert(leaderboardData)
    }

    override fun clearLeaderboardData() {
        quizDao.clearLeaderboardData()
    }

    override suspend fun getLeaderboardData(): List<Leaderboard> {
        return quizDao.getLeaderboardData()
    }

    override suspend fun fetchAndStoreQuizzes() {
        try {
            val response = quizApiService.fetchQuestions()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null) {
                    val quiz = mapApiResponseToQuiz(apiResponse)
                    insert(quiz)
                } else {
                    Log.e("QuizRepositoryImpl", "Response body is null")
                }
            } else {
                Log.e("QuizRepositoryImpl", "Error: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("QuizRepositoryImpl", "Error fetching quizzes", e)
        }
    }
}

