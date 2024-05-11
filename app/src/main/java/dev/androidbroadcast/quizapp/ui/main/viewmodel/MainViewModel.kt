package dev.androidbroadcast.quizapp.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dev.androidbroadcast.quizapp.data.model.Leaderboard
import dev.androidbroadcast.quizapp.data.model.Quiz
import dev.androidbroadcast.quizapp.data.repository.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {

    val mQuizRepository: QuizRepository = QuizRepository(application)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _listQuiz = MutableLiveData<List<Quiz>>()
    lateinit var listQuiz: LiveData<List<Quiz>>

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

            mQuizRepository.fetchAndStoreQuizzes()

            _listQuiz.postValue(mQuizRepository.getQuizData())

            _loading.postValue(false)
        }
    }

    fun deleteQuizData(selectedItems: IntArray) {
        for (i in selectedItems) {
            listQuiz.value?.get(i)?.let { mQuizRepository.delete(it) }
        }
    }

    fun getQuizData() {
        listQuiz = liveData {
            _loading.value = true
            emit(mQuizRepository.getQuizData())
            _loading.value = false
        }
    }

    fun replaceTopicData(quizData: List<Quiz>) {
        _listQuiz.value = quizData
        listQuiz = _listQuiz
    }

    fun clearLeaderboardData() {
        mQuizRepository.clearLeaderboardData()
    }

    fun getLeaderboardData() {
        listLeaderboard = liveData {
            _loading.value = true
            emit(mQuizRepository.getLeaderboardData())
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
