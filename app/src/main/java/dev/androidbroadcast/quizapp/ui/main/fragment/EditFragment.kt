package dev.androidbroadcast.quizapp.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.androidbroadcast.quizapp.R
import dev.androidbroadcast.quizapp.data.model.Quiz
import dev.androidbroadcast.quizapp.databinding.CardRowItemBinding
import dev.androidbroadcast.quizapp.databinding.FragmentEditBinding
import dev.androidbroadcast.quizapp.ui.edit.activity.EditTitleActivity
import dev.androidbroadcast.quizapp.ui.helper.ViewModelFactory
import dev.androidbroadcast.quizapp.ui.main.adapter.EditAdapter
import dev.androidbroadcast.quizapp.ui.main.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    private lateinit var viewModel: MainViewModel
    private var listQuiz = listOf<Quiz>()
    private val adapter = EditAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(layoutInflater)
        obtainViewModel(requireActivity() as AppCompatActivity)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        setRecyclerView()
        setOnClickAdapter()
        setButton()
        onBackButtonPressed()
    }

    override fun onPause() {
        super.onPause()
        hideDeleteSelection()
    }

    private fun obtainViewModel(activity: AppCompatActivity) {
        val factory = ViewModelFactory.getInstance(activity.application)
        viewModel = ViewModelProvider(activity, factory)[MainViewModel::class.java]
    }

    private fun getData() {
        viewLifecycleOwner.lifecycleScope.launch {
            var isLoading = true
            @Suppress("KotlinConstantConditions")
            viewModel.setLoading(isLoading)
            withContext(Dispatchers.IO) {
                listQuiz = viewModel.mQuizRepository.getQuizData()
                isLoading = false
            }
            adapter.setListQuiz(listQuiz)
            viewModel.replaceTopicData(listQuiz)
            if (listQuiz.isEmpty()) {
                binding.emptyScreen.visibility = View.VISIBLE
            } else {
                binding.emptyScreen.visibility = View.GONE
            }
            viewModel.setLoading(isLoading)
        }
    }

    private fun observeData() {
        viewModel.loading.observe(viewLifecycleOwner) {
            setLoading(it)
        }
        viewModel.listQuiz.observe(viewLifecycleOwner) {
            adapter.setListQuiz(it)
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

    private val requestTopicLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        getData()
    }

    private fun launchActivity(quizData: Quiz) {
        val intent = Intent(
            requireActivity(), EditTitleActivity::class.java
        )

        intent.putExtra(EditTitleActivity.EXTRA_QUIZ, quizData)
        intent.putExtra(EditTitleActivity.EXTRA_STATUS, EDIT)

        requestTopicLauncher.launch(intent)
    }

    private fun setLoading(status: Boolean) {
        if (status) {
            binding.loadingScreen.visibility = View.VISIBLE
        } else {
            binding.loadingScreen.visibility = View.GONE
        }
    }

    private fun setDeleteMenu(status: Boolean) {
        if (status) {
            binding.appBar.menu.clear()
            binding.appBar.inflateMenu(R.menu.select_all_menu)
        } else {
            binding.appBar.menu.clear()
            binding.appBar.inflateMenu(R.menu.edit_appbar_menu_variant)
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
        adapter.setOnItemClickCallback(object : EditAdapter.OnItemClickCallback {
            override fun onItemClicked(quizData: Quiz) {
                launchActivity(quizData)
            }
        })

        adapter.setOnItemLongClickCallback(object : EditAdapter.OnItemLongClickCallback {
            override fun onItemLongClicked(cardBinding: CardRowItemBinding) {
                showDeleteSelection()
                cardBinding.checkbox.isChecked = true
            }
        })
    }

    private fun showDeleteSelection() {
        with(binding) {
            addButton.visibility = View.GONE
            guideline.setGuidelineEnd(resources.getDimensionPixelSize(R.dimen.bottom_bar_margin))
            bottomBar.visibility = View.VISIBLE
        }
        viewModel.changeDeleteButtonStatus(true)
    }

    private fun hideDeleteSelection() {
        with(binding) {
            guideline.setGuidelineEnd(0)
            bottomBar.visibility = View.GONE
            addButton.visibility = View.VISIBLE
        }
        adapter.selectAllStatus = false
        viewModel.changeDeleteButtonStatus(false)
    }

    private fun setButton() {
        with(binding) {
            addButton.setOnClickListener {
                requestTopicLauncher.launch(Intent(activity, EditTitleActivity::class.java))
            }

            appBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete -> {
                        showDeleteSelection()
                        true
                    }

                    R.id.refresh -> {
                        getData()
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
                Log.e("Delete", "Delete")

                fun delete() {
                    val selectedItems = adapter.getSelected()
                    viewModel.deleteQuizData(selectedItems)
                    getData()
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
        parentFragmentManager.popBackStack()
    }

    companion object {
        const val ADD = 0
        const val EDIT = 1
    }
}
