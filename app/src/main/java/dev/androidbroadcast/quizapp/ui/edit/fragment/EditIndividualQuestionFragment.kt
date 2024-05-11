package dev.androidbroadcast.quizapp.ui.edit.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.androidbroadcast.quizapp.R
import dev.androidbroadcast.quizapp.data.model.Question
import dev.androidbroadcast.quizapp.databinding.FragmentEditIndividualQuestionBinding
import kotlin.properties.Delegates

class EditIndividualQuestionFragment : Fragment() {
    private lateinit var binding: FragmentEditIndividualQuestionBinding
    private var img: String? = null
    private var color: Int? = null
    private lateinit var questionData: Question
    private var status by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditIndividualQuestionBinding.inflate(layoutInflater)
        getData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInsets()
        setHeader()
        setNextButton()
        setCancelButton()
        onBackButtonPressed()
    }

    private fun getData() {
        img = arguments?.getString(EXTRA_IMAGE).toString()
        color = arguments?.getInt(EXTRA_COLOR)
        questionData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(EXTRA_QUESTION, Question::class.java) ?: Question()
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(EXTRA_QUESTION) ?: Question()
        }
        status = arguments?.getInt(EXTRA_STATUS) ?: ADD
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment)
            commit()
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
                context?.let {
                    Glide.with(it)
                        .load(img!!.toUri())
                        .into(backgroundHeader)
                }
                backgroundHeader.alpha = 0.3F
            }

            if (status == EDIT) {
                appBar.title = getString(R.string.edit_question)
                questionEditText.setText(questionData.question)

                if (questionData.type == 0) {
                    questionType.setText(resources.getString(R.string.multiple_choice))
                } else {
                    questionType.setText(resources.getString(R.string.short_form))
                }

                inputQuestionType.visibility = View.GONE
            }
        }
    }

    private fun setNextButton() {
        with(binding) {
            var isEmptyField: Boolean

            nextButton.setOnClickListener {
                questionData.question = questionEditText.text.toString().trim()

                if (questionData.question.isEmpty()) {
                    inputQuestion.error = "Question cannot be empty"
                } else {
                    inputQuestion.error = null
                }

                if (questionType.text.toString().trim().isEmpty()) {
                    inputQuestionType.error = "Question type cannot be empty"
                } else {
                    inputQuestionType.error = null
                }

                if (questionType.text.toString() == resources.getString(R.string.multiple_choice)) {
                    questionData.type = 0
                } else if (questionType.text.toString() == resources.getString(R.string.short_form)) {
                    questionData.type = 1
                }

                isEmptyField =
                    questionData.question.isEmpty() || questionType.text.toString().trim().isEmpty()

                if (!isEmptyField) {
                    val bundle = Bundle()
                    bundle.putString(EditAnswerFragment.EXTRA_IMAGE, img)
                    bundle.putInt(EditAnswerFragment.EXTRA_COLOR, color ?: 0)
                    bundle.putString(EditAnswerFragment.EXTRA_QUESTION, questionData.question)
                    bundle.putInt(EditAnswerFragment.EXTRA_QUESTION_TYPE, questionData.type)

                    if (status == EDIT) {
                        bundle.putParcelableArray(
                            EditAnswerFragment.EXTRA_ANSWER,
                            questionData.answers
                        )
                    }

                    val fragment = EditAnswerFragment()
                    fragment.arguments = bundle

                    loadFragment(fragment)
                } else {
                    context?.let {
                        MaterialAlertDialogBuilder(it)
                            .setTitle("Empty field")
                            .setMessage("Question and question type cannot be empty.")
                            .setPositiveButton("Okay") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
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
        requireActivity().onBackPressedDispatcher.addCallback {
            onBackButtonAction()
        }
    }

    private fun onBackButtonAction() {
        activity?.finish()
    }

    companion object {
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_COLOR = "extra_color"
        const val EXTRA_QUESTION = "extra_question"
        const val EXTRA_STATUS = "extra_status"
        const val ADD = 0
        const val EDIT = 1
    }
}
