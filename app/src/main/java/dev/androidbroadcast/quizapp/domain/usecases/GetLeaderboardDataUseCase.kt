package dev.androidbroadcast.quizapp.domain.usecases

import dev.androidbroadcast.quizapp.domain.model.Leaderboard
import dev.androidbroadcast.quizapp.domain.repository.QuizRepository

class GetLeaderboardDataUseCase(private val quizRepository: QuizRepository) {

    suspend fun execute(): List<Leaderboard> {
        return quizRepository.getLeaderboardData()
    }
}
