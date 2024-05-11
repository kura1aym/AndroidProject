package dev.androidbroadcast.quizapp.domain.usecases

import dev.androidbroadcast.quizapp.domain.model.Leaderboard
import dev.androidbroadcast.quizapp.domain.repository.QuizRepository

class InsertLeaderboardUseCase(private val quizRepository: QuizRepository) {
    fun execute(leaderboardData: Leaderboard) {
        quizRepository.insert(leaderboardData)
    }
}
