package dev.androidbroadcast.quizapp.domain.usecases

import dev.androidbroadcast.quizapp.domain.model.Quiz
import dev.androidbroadcast.quizapp.domain.repository.QuizRepository

class UpdateQuizUseCase(private val quizRepository: QuizRepository) {

    fun execute(quiz: Quiz) {
        quizRepository.update(quiz)
    }
}

