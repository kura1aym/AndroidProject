package dev.androidbroadcast.quizapp.ui.edit.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.androidbroadcast.quizapp.data.model.Question
import dev.androidbroadcast.quizapp.data.model.Quiz
import dev.androidbroadcast.quizapp.data.repository.QuizRepository

class EditQuestionViewModel(application: Application) : ViewModel() {
    private val mQuizRepository: QuizRepository = QuizRepository(application)

    fun insert(quizData: Quiz) {
        mQuizRepository.insert(quizData)
    }

    fun update(quizData: Quiz) {
        mQuizRepository.update(quizData)
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
