package dev.androidbroadcast.quizapp.ui.helper

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.androidbroadcast.quizapp.ui.edit.viewmodel.EditQuestionViewModel
import dev.androidbroadcast.quizapp.ui.main.viewmodel.MainViewModel
import dev.androidbroadcast.quizapp.ui.play.viewmodel.PlayViewModel

class ViewModelFactory private constructor(private val mApplication: Application) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(mApplication) as T
        } else if (modelClass.isAssignableFrom(EditQuestionViewModel::class.java)) {
            return EditQuestionViewModel(mApplication) as T
        } else if (modelClass.isAssignableFrom(PlayViewModel::class.java)) {
            return PlayViewModel(mApplication) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(application: Application): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(application)
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}

