package dev.androidbroadcast.quizapp.ui.edit.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.androidbroadcast.quizapp.R
import dev.androidbroadcast.quizapp.databinding.ActivityEditIndividualAnswerBinding
import dev.androidbroadcast.quizapp.domain.model.Answer
import kotlin.properties.Delegates

class EditIndividualAnswerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditIndividualAnswerBinding
    private var img: String? = null
    private var color: Int? = null
    private var questionType by Delegates.notNull<Int>()
    private lateinit var answerData: Answer
    private var status by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        getData()
        setColor()
        binding = ActivityEditIndividualAnswerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setInsets()
        setHeader()
        setAllowedInput()
        setNextButton()
        setCancelButton()
        onBackButtonPressed()
    }

    private fun getData() {
        img = intent.getStringExtra(EXTRA_IMAGE)
        color = intent.getIntExtra(EXTRA_COLOR, 0)
        questionType = intent.getIntExtra(EXTRA_QUESTION_TYPE, 0)
        answerData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_ANSWER, Answer::class.java) ?: Answer()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_ANSWER) ?: Answer()
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

    private fun setInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomInset) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.updatePadding(bottom = insets.bottom)

            WindowInsetsCompat.CONSUMED
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomBar) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.ime())

            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = if (insets.bottom > 0) {
                    insets.bottom - binding.bottomInset.height
                } else {
                    insets.bottom
                }
            }

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setHeader() {
        with(binding) {
            if (img != "null") {
                Glide.with(this@EditIndividualAnswerActivity)
                    .load(img!!.toUri())
                    .into(backgroundHeader)
                backgroundHeader.alpha = 0.3F
            }

            if (status == EDIT) {
                appBar.title = getString(R.string.edit_answer)
                answerEditText.setText(answerData.answer)
                correctAnswerCheck.isChecked = answerData.isCorrect
            }
        }
    }

    private fun setAllowedInput() {
        with(binding) {
            if (questionType == 1) {
                correctAnswerCheck.visibility = View.GONE
            }
        }
    }

    private fun setNextButton() {
        var isEmptyField: Boolean

        with(binding) {
            saveButton.setOnClickListener {
                answerData.answer = answerEditText.text.toString().trim()

                answerData.isCorrect = if (questionType == 0) {
                    correctAnswerCheck.isChecked
                } else {
                    true
                }

                if (answerData.answer.isEmpty()) {
                    inputAnswer.error = "Answer cannot be empty"
                } else {
                    inputAnswer.error = null
                }

                isEmptyField = answerData.answer.isEmpty()

                if (!isEmptyField) {
                    val resultIntent = Intent()
                    resultIntent.putExtra(EXTRA_RESULT_VALUE, answerData)
                    setResult(RESULT_CODE, resultIntent)
                    finish()
                } else {
                    MaterialAlertDialogBuilder(this@EditIndividualAnswerActivity)
                        .setTitle("Empty field")
                        .setMessage("Answer cannot be empty.")
                        .setPositiveButton("Okay") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }
    }

    private fun setCancelButton() {
        with(binding) {
            cancelButton.setOnClickListener {
                onBackButtonAction()
            }
        }
    }

    private fun onBackButtonPressed() {
        onBackPressedDispatcher.addCallback {
            onBackButtonAction()
        }
    }

    private fun onBackButtonAction() {
        finish()
    }

    companion object {
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_COLOR = "extra_color"
        const val EXTRA_QUESTION_TYPE = "extra_question_type"
        const val EXTRA_ANSWER = "extra_answer"
        const val EXTRA_STATUS = "status"
        const val ADD = 0
        const val EDIT = 1
        const val RESULT_CODE = 200
        const val EXTRA_RESULT_VALUE = "extra_result_value"
    }
}
