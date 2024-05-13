package dev.androidbroadcast.quizapp.ui.play.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import dev.androidbroadcast.quizapp.R
import dev.androidbroadcast.quizapp.data.model.Play
import dev.androidbroadcast.quizapp.data.model.Quiz
import dev.androidbroadcast.quizapp.data.model.SidebarData
import dev.androidbroadcast.quizapp.databinding.FragmentPlayTitleBinding


class PlayTitleFragment : Fragment() {
    private lateinit var binding: FragmentPlayTitleBinding
    private lateinit var quizData: Quiz

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayTitleBinding.inflate(layoutInflater)
        getData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInsets()
        setContent()
        setButton()
    }

    private fun getData() {
        quizData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(EXTRA_QUIZ, Quiz::class.java) ?: Quiz()
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(EXTRA_QUIZ) ?: Quiz()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment)
            addToBackStack("PlayTitleFragment")
            commit()
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
            if (quizData.img != null) {
                context?.let {
                    Glide.with(it)
                        .load(quizData.img)
                        .into(backgroundHeader)
                }
                backgroundHeader.alpha = 0.3F
            }
            title.text = quizData.title
            contentDescription.text = quizData.desc
        }
    }

    private fun setButton() {
        with(binding) {
            playButton.setOnClickListener {
                val bundle = Bundle()
                var questionStatus = arrayOf<SidebarData>()

                for (i in quizData.questions) {
                    questionStatus += SidebarData()
                }

                if (quizData.questions[0].type == 0) {
                    bundle.putParcelable(
                        PlayMultipleChoiceFragment.EXTRA_PLAY,
                        Play(
                            quizData.img?.toUri(),
                            quizData.title,
                            quizData.questions,
                            questionStatus
                        )
                    )

                    val fragment = PlayMultipleChoiceFragment()
                    fragment.arguments = bundle

                    loadFragment(fragment)
                } else {
                    bundle.putParcelable(
                        PlayShortFormFragment.EXTRA_PLAY,
                        Play(
                            quizData.img?.toUri(),
                            quizData.title,
                            quizData.questions,
                            questionStatus
                        )
                    )

                    val fragment = PlayShortFormFragment()
                    fragment.arguments = bundle

                    loadFragment(fragment)
                }
            }

            cancelButton.setOnClickListener {
                activity?.finish()
            }
        }
    }

    companion object {
        const val EXTRA_QUIZ = "extra_quiz"
    }
}
