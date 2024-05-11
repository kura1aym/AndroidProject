package dev.androidbroadcast.quizapp.domain.usecases

import dev.androidbroadcast.quizapp.domain.repository.QuizRepository

class ClearLeaderboardDataUseCase(private val quizRepository: QuizRepository) {
    fun execute() {
        quizRepository.clearLeaderboardData()
    }
}
