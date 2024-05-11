package dev.androidbroadcast.quizapp.ui.edit.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import dev.androidbroadcast.quizapp.R
import dev.androidbroadcast.quizapp.domain.model.Question
import dev.androidbroadcast.quizapp.databinding.ActivityEditIndividualQuestionBinding
import dev.androidbroadcast.quizapp.ui.edit.fragment.EditIndividualQuestionFragment
import kotlin.properties.Delegates

class EditIndividualQuestionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditIndividualQuestionBinding
    private var img: String? = null
    private var color: Int? = null
    private lateinit var questionData: Question
    private var status by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        getData()
        setColor()
        binding = ActivityEditIndividualQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadFragment()
    }

    private fun getData() {
        img = intent.getStringExtra(EXTRA_IMAGE)
        color = intent.getIntExtra(EXTRA_COLOR, 0)
        questionData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_QUESTION, Question::class.java) ?: Question()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_QUESTION) ?: Question()
        }
        status = intent.getIntExtra(EXTRA_STATUS, ADD)
    }

    private fun setColor() {
        if (color != 0) {
            DynamicColors.applyToActivityIfAvailable(
                this,
                DynamicColorsOptions.Builder()
                    .setContentBasedSource(color!!)
                    .build()
            )
        }
    }

    private fun loadFragment() {
        val bundle = Bundle()
        bundle.putString(EditIndividualQuestionFragment.EXTRA_IMAGE, img)
        bundle.putInt(EditIndividualQuestionFragment.EXTRA_COLOR, color ?: 0)
        bundle.putParcelable(EditIndividualQuestionFragment.EXTRA_QUESTION, questionData)
        bundle.putInt(EditIndividualQuestionFragment.EXTRA_STATUS, status)

        val fragment = EditIndividualQuestionFragment()
        fragment.arguments = bundle

        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment)
            commit()
        }
    }

    companion object {
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_COLOR = "extra_color"
        const val EXTRA_QUESTION = "extra_question"
        const val EXTRA_STATUS = "extra_status"
        const val ADD = 0
        const val EDIT = 1
        const val EXTRA_RESULT_VALUE = "extra_result_value"
    }
}
