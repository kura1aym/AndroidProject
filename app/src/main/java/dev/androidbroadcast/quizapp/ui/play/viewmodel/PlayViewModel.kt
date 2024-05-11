package dev.androidbroadcast.quizapp.ui.play.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import dev.androidbroadcast.quizapp.data.model.Leaderboard
import dev.androidbroadcast.quizapp.data.repository.QuizRepository


class PlayViewModel(application: Application) : ViewModel() {
    private val mQuizRepository: QuizRepository = QuizRepository(application)

    fun insert(leaderboardData: Leaderboard) {
        mQuizRepository.insert(leaderboardData)
    }
}
