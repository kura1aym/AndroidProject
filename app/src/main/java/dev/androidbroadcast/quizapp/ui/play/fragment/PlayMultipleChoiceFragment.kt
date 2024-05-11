package dev.androidbroadcast.quizapp.ui.play.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.androidbroadcast.quizapp.R
import dev.androidbroadcast.quizapp.data.model.Answer
import dev.androidbroadcast.quizapp.data.model.Play
import dev.androidbroadcast.quizapp.databinding.FragmentPlayMultipleChoiceBinding
import dev.androidbroadcast.quizapp.ui.play.adapter.MultipleChoiceAdapter
import dev.androidbroadcast.quizapp.ui.play.adapter.MultipleChoiceVariantAdapter
import dev.androidbroadcast.quizapp.ui.play.adapter.SidebarAdapter
import kotlin.properties.Delegates

class PlayMultipleChoiceFragment : Fragment() {
    private lateinit var binding: FragmentPlayMultipleChoiceBinding
    private val adapter = MultipleChoiceAdapter()
    private val adapterVariant = MultipleChoiceVariantAdapter()
    private val sidebarAdapter = SidebarAdapter()
    private lateinit var playData: Play
    private var playingIndex by Delegates.notNull<Int>()
    private var isSidebarOpen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayMultipleChoiceBinding.inflate(layoutInflater)
        getData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPlayingIndex()
        setInsets()
        setContent()
        setAppbarButton()
        setRecyclerView()
        setOnClickAdapter()
        setNextButton()
        setSkipButton()
        onBackButtonPressed()
    }

    override fun onResume() {
        super.onResume()
        onBackButtonPressed()
    }

    private fun getData() {
        playData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(EXTRA_PLAY, Play::class.java) ?: Play()
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(EXTRA_PLAY) ?: Play()
        }
    }

    private fun setPlayingIndex() {
        with(playData) {
            playingIndex = if (!timeForSkippedQuestion) {
                currentIndex
            } else {
                if (currentIndex < skippedQuestion.size) {
                    skippedQuestion[currentIndex]
                } else {
                    questions.size
                }
            }
        }
    }

    private fun setInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomInset) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.updatePadding(bottom = insets.bottom)

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setContent() {
        with(binding) {
            with(playData) {
                if (playData.img != null) {
                    context?.let {
                        Glide.with(it)
                            .load(playData.img)
                            .into(backgroundHeader)
                    }
                    backgroundHeader.alpha = 0.3F
                }

                appBar.title = questions[playingIndex].question

                if (playData.questions[playingIndex].multipleCorrectAnswer) {
                    nextButton.visibility = View.VISIBLE
                    if (playingIndex == questions.size - 1 && skippedQuestion.isEmpty()) {
                        nextButton.text = getString(R.string.finish)
                    } else if (skippedQuestion.isNotEmpty()) {
                        if (playingIndex == skippedQuestion[skippedQuestion.size - 1]) {
                            nextButton.text = getString(R.string.finish)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun openSidebar() {
        with(binding) {
            sidebar.visibility = View.VISIBLE
            sidebar.setOnTouchListener { v, _ ->
                v.parent.requestDisallowInterceptTouchEvent(true)
                true
            }
        }
        isSidebarOpen = true
    }

    private fun closeSidebar() {
        with(binding) {
            sidebar.visibility = View.GONE
        }
        isSidebarOpen = false
    }

    private fun setAppbarButton() {
        with(binding) {
            appBar.setNavigationOnClickListener {
                onBackButtonAction()
            }
            appBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.sidebarMenuItem -> {
                        openSidebar()
                        true
                    }

                    else -> true
                }
            }
            sidebarAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.sidebarMenuItem -> {
                        closeSidebar()
                        true
                    }

                    else -> true
                }
            }
        }
    }

    private fun setRecyclerView() {
        with(binding) {
            val layoutManager = LinearLayoutManager(requireContext())
            recyclerView.layoutManager = layoutManager

            val sidebarLayoutManager = GridLayoutManager(requireContext(), 5)
            sidebarRecyclerView.layoutManager = sidebarLayoutManager

            with(playData) {
                if (questions[playingIndex].multipleCorrectAnswer) {
                    adapterVariant.setListAnswer(questions[playingIndex].answers.toList())
                    recyclerView.adapter = adapterVariant
                } else {
                    adapter.setListAnswer(questions[playingIndex].answers.toList())
                    recyclerView.adapter = adapter
                }

                questionStatus[playingIndex].isActive = true

                sidebarAdapter.setListSidebarData(questionStatus.toList())
                sidebarRecyclerView.adapter = sidebarAdapter
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment)
            commit()
        }
    }

    private fun nextQuestion() {
        val bundle = Bundle()

        with(playData) {
            if (questions[playingIndex].type == 0) {
                bundle.putParcelable(EXTRA_PLAY, playData)

                val fragment = PlayMultipleChoiceFragment()
                fragment.arguments = bundle

                loadFragment(fragment)
            } else {
                bundle.putParcelable(PlayShortFormFragment.EXTRA_PLAY, playData)

                val fragment = PlayShortFormFragment()
                fragment.arguments = bundle

                loadFragment(fragment)
            }
        }
    }

    private fun loadFinishFragment() {
        val bundle = Bundle()
        bundle.putParcelable(FinishFragment.EXTRA_PLAY, playData)

        val fragment = FinishFragment()
        fragment.arguments = bundle

        loadFragment(fragment)
    }

    private fun nextAction() {
        with(playData) {
            if (!timeForSkippedQuestion) {
                if (playingIndex < questions.size) {
                    nextQuestion()
                } else if (skippedQuestion.isNotEmpty()) {
                    timeForSkippedQuestion = true
                    currentIndex = 0
                    setPlayingIndex()
                    nextQuestion()
                } else {
                    loadFinishFragment()
                }
            } else {
                if (playingIndex < skippedQuestion[skippedQuestion.size - 1] + 1) {
                    nextQuestion()
                } else {
                    loadFinishFragment()
                }
            }
        }
    }

    private fun setOnClickAdapter() {
        adapter.setOnItemClickCallback(object : MultipleChoiceAdapter.OnItemClickCallback {
            override fun onItemClicked(answerData: Answer) {
                if (answerData.isCorrect) {
                    playData.correctAnswer++
                }
                playData.questionStatus[playingIndex].isAnswered = true
                playData.questionStatus[playingIndex].isActive = false
                playData.currentIndex++
                setPlayingIndex()
                nextAction()
            }
        })
    }

    private fun setNextButton() {
        binding.nextButton.setOnClickListener {
            val selectedItems = adapterVariant.getSelected()
            with(playData.questions[playingIndex]) {
                var score = 0F
                for (i in selectedItems) {
                    if (answers[i].isCorrect) {
                        score++
                    } else {
                        score--
                    }
                }

                var correctAnswerSize = 0
                for (i in answers) {
                    if (i.isCorrect) {
                        correctAnswerSize++
                    }
                }

                score /= correctAnswerSize
                if (score > 0) {
                    playData.correctAnswer += score
                }
            }
            playData.questionStatus[playingIndex].isAnswered = true
            playData.questionStatus[playingIndex].isActive = false
            playData.currentIndex++
            setPlayingIndex()
            nextAction()
        }
    }

    private fun setSkipButton() {
        with(playData) {
            if (currentIndex == questions.size - 1 || timeForSkippedQuestion) {
                binding.skipButton.visibility = View.GONE
            } else {
                binding.skipButton.setOnClickListener {
                    skippedQuestion += currentIndex
                    questionStatus[playingIndex].isActive = false
                    currentIndex++
                    setPlayingIndex()
                    nextQuestion()
                }
            }
        }
    }

    private fun onBackButtonPressed() {
        requireActivity().onBackPressedDispatcher.addCallback {
            if (isSidebarOpen) {
                closeSidebar()
            } else {
                onBackButtonAction()
            }
        }
    }

    private fun onBackButtonAction() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Exit?")
                .setMessage("This will finish the quiz.")
                .setPositiveButton("Yes") { _, _ ->
                    loadFinishFragment()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    companion object {
        const val EXTRA_PLAY = "extra_play"
    }
}
