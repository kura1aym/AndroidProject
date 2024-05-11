package dev.androidbroadcast.quizapp.domain.usecases

import dev.androidbroadcast.quizapp.domain.model.Quiz
import dev.androidbroadcast.quizapp.domain.repository.QuizRepository

class GetQuizDataUseCase(private val quizRepository: QuizRepository) {

    suspend fun execute(): List<Quiz> {
        return quizRepository.getQuizData()
    }
}

