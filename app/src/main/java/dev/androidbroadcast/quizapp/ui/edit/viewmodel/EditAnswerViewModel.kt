package dev.androidbroadcast.quizapp.ui.edit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.androidbroadcast.quizapp.data.model.Answer

class EditAnswerViewModel : ViewModel() {
    private val _answer = MutableLiveData<List<Answer>>()
    val listAnswer: LiveData<List<Answer>> = _answer

    private val _deleteButtonStatus = MutableLiveData<Boolean>()
    val deleteButtonStatus: LiveData<Boolean> = _deleteButtonStatus

    fun insertAnswer(answerData: Array<Answer>) {
        _answer.value = answerData.toList()
    }

    fun changeDeleteButtonStatus(deleteButtonStatus: Boolean) {
        _deleteButtonStatus.value = deleteButtonStatus
    }

    fun getDeleteButtonStatus(): Boolean {
        return deleteButtonStatus.value ?: false
    }
}
