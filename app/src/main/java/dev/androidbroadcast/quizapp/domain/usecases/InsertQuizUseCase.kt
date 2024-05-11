package dev.androidbroadcast.quizapp.domain.usecases

import dev.androidbroadcast.quizapp.domain.model.Quiz
import dev.androidbroadcast.quizapp.domain.repository.QuizRepository

class InsertQuizUseCase(private val quizRepository: QuizRepository) {
    fun execute(quizData: Quiz) {
        quizRepository.insert(quizData)
    }
}
