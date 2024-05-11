package dev.androidbroadcast.quizapp.ui.edit.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.androidbroadcast.quizapp.R
import dev.androidbroadcast.quizapp.domain.model.Question
import dev.androidbroadcast.quizapp.databinding.CardRowItemBinding
import dev.androidbroadcast.quizapp.databinding.FragmentEditAnswerBinding
import dev.androidbroadcast.quizapp.domain.model.Answer
import dev.androidbroadcast.quizapp.ui.edit.activity.EditIndividualAnswerActivity
import dev.androidbroadcast.quizapp.ui.edit.activity.EditIndividualQuestionActivity
import dev.androidbroadcast.quizapp.ui.edit.adapter.AnswerAdapter
import dev.androidbroadcast.quizapp.ui.edit.viewmodel.EditAnswerViewModel
import kotlin.properties.Delegates

class EditAnswerFragment : Fragment() {
    private lateinit var binding: FragmentEditAnswerBinding
    private val viewModel by viewModels<EditAnswerViewModel>()
    private val adapter = AnswerAdapter()
    private var img: String? = null
    private var color: Int? = null
    private lateinit var question: String
    private var questionType by Delegates.notNull<Int>()
    private lateinit var answerData: Array<Answer>
    private var itemPosition by Delegates.notNull<Int>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditAnswerBinding.inflate(layoutInflater)
        getData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInsets()
        setHeader()
        observeData()
        setRecyclerView()
        setOnClickAdapter()
        setButton()
        onBackButtonPressed()
    }

    override fun onResume() {
        super.onResume()
        onBackButtonPressed()
    }

    private fun getData() {
        img = arguments?.getString(EXTRA_IMAGE)
        color = arguments?.getInt(EXTRA_COLOR)
        question = arguments?.getString(EXTRA_QUESTION).toString()
        questionType = arguments?.getInt(EXTRA_QUESTION_TYPE) ?: 0
        answerData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArray(EXTRA_ANSWER, Answer::class.java) ?: arrayOf()
        } else {
            @Suppress("DEPRECATION", "UNCHECKED_CAST")
            arguments?.getParcelableArray(EXTRA_ANSWER) as Array<Answer>?
                ?: arrayOf()
        }
        viewModel.insertAnswer(answerData)
    }

    private val requestAddAnswerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == EditIndividualAnswerActivity.RESULT_CODE && it.data != null) {
            answerData += if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getParcelableExtra(
                    EditIndividualAnswerActivity.EXTRA_RESULT_VALUE,
                    Answer::class.java
                ) ?: Answer()
            } else {
                @Suppress("DEPRECATION")
                it.data?.getParcelableExtra(EditIndividualAnswerActivity.EXTRA_RESULT_VALUE) ?: Answer()
            }
            viewModel.insertAnswer(answerData)
        }
    }

    private val requestEditAnswerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == EditIndividualAnswerActivity.RESULT_CODE && it.data != null) {
            answerData[itemPosition] = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getParcelableExtra(
                    EditIndividualAnswerActivity.EXTRA_RESULT_VALUE,
                    Answer::class.java
                ) ?: Answer()
            } else {
                @Suppress("DEPRECATION")
                it.data?.getParcelableExtra(EditIndividualAnswerActivity.EXTRA_RESULT_VALUE) ?: Answer()
            }
            viewModel.insertAnswer(answerData)
        }
    }

    private fun launchActivity(status: Int) {
        val intent = Intent(activity, EditIndividualAnswerActivity::class.java)
        intent.putExtra(EditIndividualAnswerActivity.EXTRA_IMAGE, img)
        intent.putExtra(EditIndividualAnswerActivity.EXTRA_COLOR, color)
        intent.putExtra(EditIndividualAnswerActivity.EXTRA_QUESTION_TYPE, questionType)
        intent.putExtra(EditIndividualAnswerActivity.EXTRA_STATUS, status)

        if (status == ADD) {
            requestAddAnswerLauncher.launch(intent)
        } else {
            intent.putExtra(EditIndividualAnswerActivity.EXTRA_ANSWER, answerData[itemPosition])
            requestEditAnswerLauncher.launch(intent)
        }
    }

    private fun setInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomInset) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.updatePadding(bottom = insets.bottom)

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
            appBar.title = question
        }
    }

    private fun observeData() {
        viewModel.listAnswer.observe(viewLifecycleOwner) {
            adapter.setListAnswer(it)
            if (it.isEmpty()) {
                binding.emptyScreen.visibility = View.VISIBLE
            } else {
                binding.emptyScreen.visibility = View.GONE
            }
        }
        viewModel.deleteButtonStatus.observe(viewLifecycleOwner) {
            adapter.setCheckbox(it)
            setDeleteMenu(it)
        }
    }

    private fun setDeleteMenu(status: Boolean) {
        if (status) {
            binding.appBar.menu.clear()
            binding.appBar.inflateMenu(R.menu.select_all_menu)
        } else {
            binding.appBar.menu.clear()
            binding.appBar.inflateMenu(R.menu.edit_appbar_menu)
        }
    }

    private fun setRecyclerView() {
        with(binding) {
            val layoutManager = LinearLayoutManager(requireContext())
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
    }

    private fun setOnClickAdapter() {
        adapter.setOnItemClickCallback(object : AnswerAdapter.OnItemClickCallback {
            override fun onItemClicked(position: Int) {
                itemPosition = position
                launchActivity(EDIT)
            }
        })

        adapter.setOnItemLongClickCallback(object : AnswerAdapter.OnItemLongClickCallback {
            override fun onItemLongClicked(cardBinding: CardRowItemBinding) {
                showDeleteSelection()
                cardBinding.checkbox.isChecked = true
            }
        })
    }

    private fun showDeleteSelection() {
        with(binding) {
            addButton.visibility = View.GONE
            saveButton.visibility = View.GONE
            deleteButton.visibility = View.VISIBLE
            cancelButton.visibility = View.VISIBLE
        }
        viewModel.changeDeleteButtonStatus(true)
    }

    private fun hideDeleteSelection() {
        with(binding) {
            addButton.visibility = View.VISIBLE
            saveButton.visibility = View.VISIBLE
            deleteButton.visibility = View.GONE
            cancelButton.visibility = View.GONE
        }
        adapter.selectAllStatus = false
        viewModel.changeDeleteButtonStatus(false)
    }

    private fun setButton() {
        with(binding) {
            addButton.setOnClickListener {
                launchActivity(ADD)
            }

            appBar.setNavigationOnClickListener {
                onBackButtonAction()
            }

            appBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete -> {
                        showDeleteSelection()
                        true
                    }

                    R.id.selectAll -> {
                        adapter.selectAll()
                        true
                    }

                    else -> false
                }
            }

            deleteButton.setOnClickListener {
                fun delete() {
                    val selectedItems = adapter.getSelected()
                    var newAnswerData = arrayOf<Answer>()
                    for (i in answerData.indices) {
                        var isDifferent = true
                        var j = 0
                        while (j < selectedItems.size && isDifferent) {
                            isDifferent = i != selectedItems[j]
                            j++
                        }
                        if (isDifferent) {
                            newAnswerData += answerData[i]
                        }
                    }
                    answerData = newAnswerData
                    viewModel.insertAnswer(answerData)
                    hideDeleteSelection()
                }

                context?.let {
                    MaterialAlertDialogBuilder(it)
                        .setTitle("Delete selected items?")
                        .setMessage("Selected items will be removed.")
                        .setPositiveButton("Yes") { _, _ ->
                            delete()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }

            cancelButton.setOnClickListener {
                hideDeleteSelection()
            }

            saveButton.setOnClickListener {
                if (answerData.isNotEmpty()) {
                    var anyCorrectAnswer = false
                    var multipleCorrectAnswer = false
                    var i = 0

                    while (i < answerData.size && !anyCorrectAnswer) {
                        anyCorrectAnswer = answerData[i].isCorrect
                        i++
                    }

                    while (i < answerData.size && !multipleCorrectAnswer) {
                        multipleCorrectAnswer = answerData[i].isCorrect
                        i++
                    }

                    if (anyCorrectAnswer) {
                        val resultIntent = Intent()
                        resultIntent.putExtra(
                            EditIndividualQuestionActivity.EXTRA_RESULT_VALUE,
                            Question(question, questionType, answerData, multipleCorrectAnswer)
                        )
                        requireActivity().setResult(RESULT_CODE, resultIntent)
                        requireActivity().finish()
                    } else {
                        context?.let {
                            MaterialAlertDialogBuilder(it)
                                .setTitle("No correct answer")
                                .setMessage("There is at least one correct answer.")
                                .setPositiveButton("Close") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                    }
                } else {
                    context?.let {
                        MaterialAlertDialogBuilder(it)
                            .setTitle("Answer is empty")
                            .setMessage("Answer cannot be empty.")
                            .setPositiveButton("Close") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun onBackButtonPressed() {
        requireActivity().onBackPressedDispatcher.addCallback {
            if (viewModel.getDeleteButtonStatus()) {
                hideDeleteSelection()
            } else {
                onBackButtonAction()
            }
        }
    }

    private fun onBackButtonAction() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Exit?")
                .setMessage("Existing data will not be saved.")
                .setPositiveButton("Yes") { _, _ ->
                    activity?.finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    companion object {
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_COLOR = "extra_color"
        const val EXTRA_QUESTION = "extra_question"
        const val EXTRA_QUESTION_TYPE = "extra_question_type"
        const val EXTRA_ANSWER = "extra_answer"
        const val ADD = 0
        const val EDIT = 1
        const val RESULT_CODE = 200
    }
}
