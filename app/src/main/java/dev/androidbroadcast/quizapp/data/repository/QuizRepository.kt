package dev.androidbroadcast.quizapp.data.repository

import android.app.Application
import android.util.Log
import dev.androidbroadcast.quizapp.data.database.QuizDao
import dev.androidbroadcast.quizapp.data.database.QuizRoomDatabase
import dev.androidbroadcast.quizapp.data.mapper.mapApiResponseToQuiz
import dev.androidbroadcast.quizapp.data.model.Leaderboard
import dev.androidbroadcast.quizapp.data.model.Quiz
import dev.androidbroadcast.quizapp.data.network.ApiClient.quizApiService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QuizRepository (application: Application) {
    private val mQuizzesDao: QuizDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = QuizRoomDatabase.getDatabase(application)
        mQuizzesDao = db.quizDao()
    }

    fun insert(quizData: Quiz) {
        executorService.execute { mQuizzesDao.insert(quizData) }
    }

    fun update(quizData: Quiz) {
        executorService.execute { mQuizzesDao.update(quizData) }
    }

    fun delete(quizData: Quiz) {
        executorService.execute { mQuizzesDao.delete(quizData) }
    }

    suspend fun getQuizData(): List<Quiz> = mQuizzesDao.getQuizData()

    fun insert(leaderboardData: Leaderboard) {
        executorService.execute { mQuizzesDao.insert(leaderboardData) }
    }

    fun clearLeaderboardData() {
        executorService.execute { mQuizzesDao.clearLeaderboardData() }
    }

    suspend fun getLeaderboardData(): List<Leaderboard> = mQuizzesDao.getLeaderboardData()


    suspend fun fetchAndStoreQuizzes() {
        val response = quizApiService.fetchQuestions()
        if (response.isSuccessful) {
            val apiResponse = response.body()
            if (apiResponse != null) {
                val quiz = mapApiResponseToQuiz(apiResponse)
                mQuizzesDao.insert(quiz)
            } else {
                Log.e("QuizRepository", "Response body is null")
            }
        } else {
            Log.e("QuizRepository", "Error: ${response.errorBody()?.string()}")
        }
    }

}


