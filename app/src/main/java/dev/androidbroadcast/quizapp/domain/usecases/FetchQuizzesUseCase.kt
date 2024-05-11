package dev.androidbroadcast.quizapp.domain.usecases

import android.util.Log
import dev.androidbroadcast.quizapp.data.repository.QuizRepositoryImpl
import dev.androidbroadcast.quizapp.domain.repository.QuizRepository

class FetchQuizzesUseCase(private val quizRepository: QuizRepository) {
    suspend fun execute() {
        try {
            (quizRepository as? QuizRepositoryImpl)?.fetchAndStoreQuizzes() // Type casting is needed to call the specific method
        } catch (e: Exception) {
            Log.e("FetchQuizzesUseCase", "Error fetching quizzes: $e")
        }
    }
}
