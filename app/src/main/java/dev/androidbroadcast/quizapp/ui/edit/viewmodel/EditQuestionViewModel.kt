package dev.androidbroadcast.quizapp.ui.edit.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.androidbroadcast.quizapp.data.database.QuizRoomDatabase
import dev.androidbroadcast.quizapp.data.network.ApiClient
import dev.androidbroadcast.quizapp.data.repository.QuizRepositoryImpl
import dev.androidbroadcast.quizapp.domain.model.Question
import dev.androidbroadcast.quizapp.domain.model.Quiz
import dev.androidbroadcast.quizapp.domain.repository.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditQuestionViewModel(application: Application) : ViewModel() {
    private val mQuizRepository: QuizRepository = QuizRepositoryImpl(
        QuizRoomDatabase.getDatabase(application).quizDao(),
        ApiClient.quizApiService
    )

    fun insert(quizData: Quiz) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mQuizRepository.insert(quizData)
            }
        }
    }


    fun update(quizData: Quiz) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mQuizRepository.update(quizData)
            }
        }
    }


    private val _question = MutableLiveData<List<Question>>()
    val listQuestion: LiveData<List<Question>> = _question

    private val _deleteButtonStatus = MutableLiveData<Boolean>()
    val deleteButtonStatus: LiveData<Boolean> = _deleteButtonStatus

    fun insertQuestion(questionData: Array<Question>) {
        _question.value = questionData.toList()
    }

    fun changeDeleteButtonStatus(deleteButtonStatus: Boolean) {
        _deleteButtonStatus.value = deleteButtonStatus
    }

    fun getDeleteButtonStatus(): Boolean {
        return deleteButtonStatus.value ?: false
    }
}
