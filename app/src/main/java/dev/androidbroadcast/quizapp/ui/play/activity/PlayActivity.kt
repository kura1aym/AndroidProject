package dev.androidbroadcast.quizapp.ui.play.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import dev.androidbroadcast.quizapp.R
import dev.androidbroadcast.quizapp.data.database.QuizRoomDatabase
import dev.androidbroadcast.quizapp.data.network.ApiClient
import dev.androidbroadcast.quizapp.data.repository.QuizRepositoryImpl
import dev.androidbroadcast.quizapp.domain.repository.QuizRepository
import dev.androidbroadcast.quizapp.databinding.ActivityPlayBinding
import dev.androidbroadcast.quizapp.domain.model.Quiz
import dev.androidbroadcast.quizapp.ui.play.fragment.PlayTitleFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayBinding
    private lateinit var quizData: Quiz
    private lateinit var quizRepository: QuizRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val quizDao = QuizRoomDatabase.getDatabase(application).quizDao()
        val quizApiService = ApiClient.quizApiService
        quizRepository = QuizRepositoryImpl(quizDao, quizApiService)
        fetchAndStoreQuizzes()
        getData()
        setColor()
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadFragment()
    }

    private fun fetchAndStoreQuizzes() {
        CoroutineScope(Dispatchers.IO).launch {
            quizRepository.fetchAndStoreQuizzes()
        }
    }

    private fun getData() {
        quizData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_QUIZ, Quiz::class.java) ?: Quiz()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_QUIZ) ?: Quiz()
        }
    }

    private fun setColor() {
        if (quizData.color != null) {
            DynamicColors.applyToActivityIfAvailable(
                this,
                DynamicColorsOptions.Builder()
                    .setContentBasedSource(quizData.color!!)
                    .build()
            )
        }
    }

    private fun loadFragment() {
        val bundle = Bundle()
        bundle.putParcelable(PlayTitleFragment.EXTRA_QUIZ, quizData)

        val fragment = PlayTitleFragment()
        fragment.arguments = bundle

        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment)
            commit()
        }
    }

    companion object {
        const val EXTRA_QUIZ = "extra_quiz"
    }
}

