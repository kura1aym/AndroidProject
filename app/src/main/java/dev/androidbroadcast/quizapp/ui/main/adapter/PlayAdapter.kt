package dev.androidbroadcast.quizapp.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.androidbroadcast.quizapp.data.model.Quiz
import dev.androidbroadcast.quizapp.databinding.CardRowItemBinding
import dev.androidbroadcast.quizapp.ui.helper.QuizDiffCallback

class PlayAdapter : RecyclerView.Adapter<PlayAdapter.TopicPlayViewHolder>() {
    private val listQuiz = ArrayList<Quiz>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setListQuiz(listQuiz: List<Quiz>) {
        val diffCallback = QuizDiffCallback(this.listQuiz, listQuiz)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listQuiz.clear()
        this.listQuiz.addAll(listQuiz)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicPlayViewHolder {
        val binding = CardRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicPlayViewHolder(parent, binding)
    }

    override fun onBindViewHolder(holder: TopicPlayViewHolder, position: Int) {
        holder.bind(listQuiz[position])
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listQuiz[position])
        }
    }

    override fun getItemCount(): Int {
        return listQuiz.size
    }

    inner class TopicPlayViewHolder(
        private val parent: ViewGroup,
        private val binding: CardRowItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(quizData: Quiz) {
            with(binding) {
                if (quizData.img != null) {
                    Glide.with(parent.context)
                        .load(quizData.img!!.toUri())
                        .into(media)
                }
                title.text = quizData.title
                title.maxLines = 1
                supportingText.text = quizData.desc
                selectButton.setOnClickListener {
                    onItemClickCallback.onItemClicked(quizData)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(quizData: Quiz)
    }
}
