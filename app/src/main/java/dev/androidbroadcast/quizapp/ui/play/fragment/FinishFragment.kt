package dev.androidbroadcast.quizapp.ui.play.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.androidbroadcast.quizapp.R
import dev.androidbroadcast.quizapp.data.model.Leaderboard
import dev.androidbroadcast.quizapp.data.model.Play
import dev.androidbroadcast.quizapp.databinding.FragmentFinishBinding
import dev.androidbroadcast.quizapp.databinding.PlayerNameInputBinding
import dev.androidbroadcast.quizapp.ui.helper.ViewModelFactory
import dev.androidbroadcast.quizapp.ui.play.viewmodel.PlayViewModel

class FinishFragment : Fragment() {
    private lateinit var binding: FragmentFinishBinding
    private lateinit var viewModel: PlayViewModel
    private lateinit var playData: Play

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinishBinding.inflate(layoutInflater)
        obtainViewModel(requireActivity() as AppCompatActivity)
        getData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInsets()
        setBackground()
        setScore()
        setButton()
        onBackButtonPressed()
    }

    override fun onResume() {
        super.onResume()
        onBackButtonPressed()
    }

    private fun obtainViewModel(activity: AppCompatActivity) {
        val factory = ViewModelFactory.getInstance(activity.application)
        viewModel = ViewModelProvider(activity, factory)[PlayViewModel::class.java]
    }

    private fun getData() {
        playData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(EXTRA_PLAY, Play::class.java) ?: Play()
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(EXTRA_PLAY) ?: Play()
        }
    }

    private fun setInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.scoreContainer) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.updatePadding(top = insets.top)

            WindowInsetsCompat.CONSUMED
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomInset) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.updatePadding(bottom = insets.bottom)

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setBackground() {
        if (playData.img != null) {
            with(binding.backgroundHeader) {
                setImageURI(playData.img)
                alpha = 0.3F
            }
        }
    }

    private fun setScore() {
        binding.score.text = buildString {
            append(((playData.correctAnswer / playData.questions.size) * 100).toInt())
            append(getString(R.string._100))
        }
    }

    private fun setButton() {
        with(binding) {
            exitButton.setOnClickListener {
                exitAction()
            }
        }
    }

    private fun onBackButtonPressed() {
        requireActivity().onBackPressedDispatcher.addCallback {
            exitAction()
        }
    }

    private fun exitAction() {
        val textInputBinding = PlayerNameInputBinding.inflate(layoutInflater)
        lateinit var leaderboardData: Leaderboard

        context?.let {
            MaterialAlertDialogBuilder(it)
                .setView(textInputBinding.root)
                .setPositiveButton("Confirm") { _, _ ->
                    leaderboardData =
                        if (textInputBinding.playerNameEditText.text?.isNotEmpty() == true) {
                            Leaderboard(
                                player = textInputBinding.playerNameEditText.text.toString(),
                                topic = playData.topic,
                                score = ((playData.correctAnswer / playData.questions.size) * 100).toInt()
                            )
                        } else {
                            Leaderboard(
                                topic = playData.topic,
                                score = ((playData.correctAnswer / playData.questions.size) * 100).toInt()
                            )
                        }
                    viewModel.insert(leaderboardData)
                    activity?.finish()
                }
                .setNegativeButton("Skip") { _, _ ->
                    leaderboardData = Leaderboard(
                        topic = playData.topic,
                        score = ((playData.correctAnswer / playData.questions.size) * 100).toInt()
                    )
                    viewModel.insert(leaderboardData)
                    activity?.finish()
                }
                .show()
        }
    }

    companion object {
        const val EXTRA_PLAY = "extra_play"
    }
}
