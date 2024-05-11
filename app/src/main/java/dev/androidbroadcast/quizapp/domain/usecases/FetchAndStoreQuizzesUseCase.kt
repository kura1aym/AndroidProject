package dev.androidbroadcast.quizapp.domain.usecases

import dev.androidbroadcast.quizapp.domain.repository.QuizRepository

class FetchAndStoreQuizzesUseCase(private val quizRepository: QuizRepository) {
    suspend fun execute() {
        quizRepository.fetchAndStoreQuizzes()
    }
}
