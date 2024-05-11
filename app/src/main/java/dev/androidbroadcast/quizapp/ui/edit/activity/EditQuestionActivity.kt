package dev.androidbroadcast.quizapp.ui.edit.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.androidbroadcast.quizapp.R
import dev.androidbroadcast.quizapp.data.model.Question
import dev.androidbroadcast.quizapp.data.model.Quiz
import dev.androidbroadcast.quizapp.databinding.ActivityEditQuestionBinding
import dev.androidbroadcast.quizapp.databinding.CardRowItemBinding
import dev.androidbroadcast.quizapp.ui.edit.adapter.QuestionAdapter
import dev.androidbroadcast.quizapp.ui.edit.fragment.EditAnswerFragment
import dev.androidbroadcast.quizapp.ui.edit.viewmodel.EditQuestionViewModel
import dev.androidbroadcast.quizapp.ui.helper.ViewModelFactory
import kotlin.properties.Delegates

class EditQuestionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditQuestionBinding
    private lateinit var viewModel: EditQuestionViewModel
    private val adapter = QuestionAdapter()
    private lateinit var quizData: Quiz
    private var status by Delegates.notNull<Int>()
    private var itemPosition by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        obtainViewModel(this)
        getData()
        setColor()
        binding = ActivityEditQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setInsets()
        setHeader()
        observeData()
        setRecyclerView()
        setOnClickAdapter()
        setButton()
        onBackButtonPressed()
    }

    private fun obtainViewModel(activity: AppCompatActivity) {
        val factory = ViewModelFactory.getInstance(activity.application)
        viewModel = ViewModelProvider(activity, factory)[EditQuestionViewModel::class.java]
    }

    private fun getData() {
        quizData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_QUIZ, Quiz::class.java) ?: Quiz()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_QUIZ) ?: Quiz()
        }
        status = intent.getIntExtra(EXTRA_STATUS, ADD)
        viewModel.insertQuestion(quizData.questions)
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

    private val requestAddQuestionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == EditAnswerFragment.RESULT_CODE && it.data != null) {
            with(quizData) {
                questions += if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.data?.getParcelableExtra(
                        EditIndividualQuestionActivity.EXTRA_RESULT_VALUE,
                        Question::class.java
                    ) ?: Question()
                } else {
                    @Suppress("DEPRECATION")
                    it.data?.getParcelableExtra(EditIndividualQuestionActivity.EXTRA_RESULT_VALUE)
                        ?: Question()
                }
                viewModel.insertQuestion(questions)
            }
        }
    }

    private val requestEditQuestionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == EditAnswerFragment.RESULT_CODE && it.data != null) {
            with(quizData) {
                questions[itemPosition] =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.data?.getParcelableExtra(
                            EditIndividualQuestionActivity.EXTRA_RESULT_VALUE, Question::class.java
                        ) ?: Question()
                    } else {
                        @Suppress("DEPRECATION")
                        it.data?.getParcelableExtra(EditIndividualQuestionActivity.EXTRA_RESULT_VALUE)
                            ?: Question()
                    }
                viewModel.insertQuestion(questions)
            }
        }
    }

    private fun launchActivity(status: Int) {
        val intent = Intent(this@EditQuestionActivity, EditIndividualQuestionActivity::class.java)
        intent.putExtra(EditIndividualQuestionActivity.EXTRA_IMAGE, quizData.img)
        intent.putExtra(EditIndividualQuestionActivity.EXTRA_COLOR, quizData.color)
        intent.putExtra(EditIndividualQuestionActivity.EXTRA_STATUS, status)

        if (status == ADD) {
            requestAddQuestionLauncher.launch(intent)
        } else {
            intent.putExtra(
                EditIndividualQuestionActivity.EXTRA_QUESTION,
                quizData.questions[itemPosition]
            )
            requestEditQuestionLauncher.launch(intent)
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
            if (quizData.img != null) {
                Glide.with(this@EditQuestionActivity)
                    .load(quizData.img!!.toUri())
                    .into(backgroundHeader)
                backgroundHeader.alpha = 0.3F
            }
            appBar.title = quizData.title
        }
    }

    private fun observeData() {
        viewModel.listQuestion.observe(this) {
            adapter.setListQuestion(it)
            if (it.isEmpty()) {
                binding.emptyScreen.visibility = View.VISIBLE
            } else {
                binding.emptyScreen.visibility = View.GONE
            }
        }
        viewModel.deleteButtonStatus.observe(this) {
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
            val layoutManager = LinearLayoutManager(this@EditQuestionActivity)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
    }

    private fun setOnClickAdapter() {
        adapter.setOnItemClickCallback(object : QuestionAdapter.OnItemClickCallback {
            override fun onItemClicked(position: Int) {
                itemPosition = position
                launchActivity(EDIT)
            }
        })

        adapter.setOnItemLongClickCallback(object : QuestionAdapter.OnItemLongClickCallback {
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

            saveButton.setOnClickListener {
                if (quizData.questions.isNotEmpty()) {
                    if (status == ADD) {
                        viewModel.insert(quizData)
                    } else {
                        viewModel.update(quizData)
                    }
                    finish()
                } else {
                    MaterialAlertDialogBuilder(this@EditQuestionActivity)
                        .setTitle("Question is empty")
                        .setMessage("Question cannot be empty.")
                        .setPositiveButton("Close") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }

            deleteButton.setOnClickListener {
                fun delete() {
                    val selectedItems = adapter.getSelected()
                    var newQuestionData = arrayOf<Question>()
                    for (i in quizData.questions.indices) {
                        var isDifferent = true
                        var j = 0
                        while (j < selectedItems.size && isDifferent) {
                            isDifferent = i != selectedItems[j]
                            j++
                        }
                        if (isDifferent) {
                            newQuestionData += quizData.questions[i]
                        }
                    }
                    quizData.questions = newQuestionData
                    viewModel.insertQuestion(quizData.questions)
                    hideDeleteSelection()
                }

                MaterialAlertDialogBuilder(this@EditQuestionActivity)
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

            cancelButton.setOnClickListener {
                hideDeleteSelection()
            }
        }
    }

    private fun onBackButtonPressed() {
        onBackPressedDispatcher.addCallback {
            if (viewModel.getDeleteButtonStatus()) {
                hideDeleteSelection()
            } else {
                onBackButtonAction()
            }
        }
    }

    private fun onBackButtonAction() {
            MaterialAlertDialogBuilder(this)
                .setTitle("Exit?")
                .setMessage("Existing data will not be saved.")
                .setPositiveButton("Yes") { _, _ ->
                    finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    companion object {
        const val EXTRA_QUIZ = "extra_quiz"
        const val EXTRA_STATUS = "extra_status"
        const val ADD = 0
        const val EDIT = 1
    }
}
