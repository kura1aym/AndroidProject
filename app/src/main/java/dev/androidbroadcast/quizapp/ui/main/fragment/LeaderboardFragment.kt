package dev.androidbroadcast.quizapp.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.androidbroadcast.quizapp.R
import dev.androidbroadcast.quizapp.databinding.FragmentLeaderboardBinding
import dev.androidbroadcast.quizapp.domain.model.Leaderboard
import dev.androidbroadcast.quizapp.ui.helper.ViewModelFactory
import dev.androidbroadcast.quizapp.ui.main.adapter.LeaderboardAdapter
import dev.androidbroadcast.quizapp.ui.main.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LeaderboardFragment : Fragment() {
    private lateinit var binding: FragmentLeaderboardBinding
    private lateinit var viewModel: MainViewModel
    private var listLeaderboard = listOf<Leaderboard>()
    private val adapter = LeaderboardAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaderboardBinding.inflate(layoutInflater)
        obtainViewModel(requireActivity() as AppCompatActivity)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        setRecyclerView()
        setButton()
        onBackButtonPressed()
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
                listLeaderboard = viewModel.quizRepository.getLeaderboardData()
                isLoading = false
            }
            adapter.setListLeaderboard(listLeaderboard)
            viewModel.replaceLeaderboardData(listLeaderboard)
            if (listLeaderboard.isEmpty()) {
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
        viewModel.listLeaderboard.observe(viewLifecycleOwner) {
            adapter.setListLeaderboard(it)
            if (it.isEmpty()) {
                binding.emptyScreen.visibility = View.VISIBLE
            } else {
                binding.emptyScreen.visibility = View.GONE
            }
        }
    }

    private fun setLoading(status: Boolean) {
        if (status) {
            binding.loadingScreen.visibility = View.VISIBLE
        } else {
            binding.loadingScreen.visibility = View.GONE
        }
    }

    private fun setRecyclerView() {
        with(binding) {
            val layoutManager = LinearLayoutManager(requireContext())
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
    }

    private fun setButton() {
        with(binding) {
            appBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.clear -> {
                        context?.let { context ->
                            MaterialAlertDialogBuilder(context)
                                .setTitle("Clear leaderboard data?")
                                .setMessage("Leaderboard data will be cleared.")
                                .setPositiveButton("Yes") { _, _ ->
                                    viewModel.clearLeaderboardData()
                                    getData()
                                }
                                .setNegativeButton("No") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun onBackButtonPressed() {
        requireActivity().onBackPressedDispatcher.addCallback {
            parentFragmentManager.popBackStack()
        }
    }
}
