package dev.androidbroadcast.quizapp.ui.play.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.androidbroadcast.quizapp.data.database.QuizRoomDatabase
import dev.androidbroadcast.quizapp.data.network.ApiClient
import dev.androidbroadcast.quizapp.data.repository.QuizRepositoryImpl
import dev.androidbroadcast.quizapp.domain.model.Leaderboard
import dev.androidbroadcast.quizapp.domain.repository.QuizRepository
import dev.androidbroadcast.quizapp.domain.usecases.InsertLeaderboardUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PlayViewModel(application: Application) : ViewModel() {
    private val mQuizRepository: QuizRepository = QuizRepositoryImpl(
        QuizRoomDatabase.getDatabase(application).quizDao(),
        ApiClient.quizApiService
    )
    private val insertLeaderboardUseCase = InsertLeaderboardUseCase(mQuizRepository)

    fun insert(leaderboardData: Leaderboard) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                insertLeaderboardUseCase.execute(leaderboardData)
            }
        }
    }
}




