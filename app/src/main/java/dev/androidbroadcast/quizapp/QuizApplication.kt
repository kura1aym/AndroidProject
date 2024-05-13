package dev.androidbroadcast.quizapp

import android.app.Application
import com.google.android.material.color.DynamicColors

class QuizApplication :  Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
