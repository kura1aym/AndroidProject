package dev.androidbroadcast.quizapp.domain.usecases

import dev.androidbroadcast.quizapp.domain.model.Quiz
import dev.androidbroadcast.quizapp.domain.repository.QuizRepository

class DeleteQuizUseCase(private val quizRepository: QuizRepository) {

    fun execute(quiz: Quiz) {
        quizRepository.delete(quiz)
    }
}
