package dev.androidbroadcast.quizapp.ui.main.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dev.androidbroadcast.quizapp.data.database.QuizRoomDatabase
import dev.androidbroadcast.quizapp.data.network.ApiClient
import dev.androidbroadcast.quizapp.data.repository.QuizRepositoryImpl
import dev.androidbroadcast.quizapp.domain.model.Leaderboard
import dev.androidbroadcast.quizapp.domain.model.Quiz
import dev.androidbroadcast.quizapp.domain.repository.QuizRepository
import dev.androidbroadcast.quizapp.domain.usecases.ClearLeaderboardDataUseCase
import dev.androidbroadcast.quizapp.domain.usecases.DeleteQuizUseCase
import dev.androidbroadcast.quizapp.domain.usecases.FetchAndStoreQuizzesUseCase
import dev.androidbroadcast.quizapp.domain.usecases.GetQuizDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {

    val quizRepository: QuizRepository = QuizRepositoryImpl(
        QuizRoomDatabase.getDatabase(application).quizDao(),
        ApiClient.quizApiService
    )

    private val fetchAndStoreQuizzesUseCase = FetchAndStoreQuizzesUseCase(quizRepository)
    private val getQuizDataUseCase = GetQuizDataUseCase(quizRepository)
    private val clearLeaderboardDataUseCase = ClearLeaderboardDataUseCase(quizRepository)
    private val deleteQuizUseCase = DeleteQuizUseCase(quizRepository)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _listQuiz = MutableLiveData<List<Quiz>>().apply { value = emptyList() }
    val listQuiz: LiveData<List<Quiz>> = _listQuiz

    private val _listLeaderboard = MutableLiveData<List<Leaderboard>>()
    lateinit var listLeaderboard: LiveData<List<Leaderboard>>

    private val _deleteButtonStatus = MutableLiveData<Boolean>()
    val deleteButtonStatus: LiveData<Boolean> = _deleteButtonStatus

    fun setLoading(status: Boolean) {
        _loading.value = status
    }

    fun fetchAndStoreQuizzes() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            try {
                fetchAndStoreQuizzesUseCase.execute()
                _listQuiz.postValue(getQuizDataUseCase.execute())
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching and storing quizzes: $e")
            }
            _loading.postValue(false)
        }
    }

    fun deleteQuizData(selectedItems: IntArray) {
        viewModelScope.launch(Dispatchers.IO) {
            for (i in selectedItems) {
                try {
                    listQuiz.value?.get(i)?.let { quiz ->
                        deleteQuizUseCase.execute(quiz)
                    }
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Error deleting quiz data: $e")
                }
            }
        }
    }

    fun getQuizData() {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val quizData = quizRepository.getQuizData()
            _listQuiz.postValue(quizData)
            _loading.postValue(false)
        }
    }

    fun replaceTopicData(quizData: List<Quiz>) {
        _listQuiz.postValue(quizData)
    }


    fun clearLeaderboardData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                clearLeaderboardDataUseCase.execute()
                _listLeaderboard.postValue(emptyList()) // Очищаем список лидеров после очистки данных
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error clearing leaderboard data: $e")
            }
        }
    }

    fun getLeaderboardData() {
        listLeaderboard = liveData {
            _loading.value = true
            emit(quizRepository.getLeaderboardData())
            _loading.value = false
        }
    }

    fun replaceLeaderboardData(leaderboardData: List<Leaderboard>) {
        _listLeaderboard.value = leaderboardData
        listLeaderboard = _listLeaderboard
    }

    fun changeDeleteButtonStatus(deleteButtonStatus: Boolean) {
        _deleteButtonStatus.value = deleteButtonStatus
    }

    fun getDeleteButtonStatus(): Boolean {
        return deleteButtonStatus.value ?: false
    }
}
