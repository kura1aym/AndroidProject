package dev.androidbroadcast.quizapp.ui.play.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.androidbroadcast.quizapp.data.model.Answer
import dev.androidbroadcast.quizapp.databinding.CardRowItemBinding

class MultipleChoiceAdapter :
    RecyclerView.Adapter<MultipleChoiceAdapter.MultipleChoiceViewHolder>() {
    private val listAnswer = ArrayList<Answer>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setListAnswer(listAnswer: List<Answer>) {
        this.listAnswer.addAll(listAnswer)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MultipleChoiceViewHolder {
        val binding =
            CardRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MultipleChoiceViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MultipleChoiceViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.bind(listAnswer[position])
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listAnswer[position])
        }
    }

    override fun getItemCount(): Int {
        return listAnswer.size
    }

    inner class MultipleChoiceViewHolder(internal val binding: CardRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(answerData: Answer) {
            with(binding) {
                title.text = answerData.answer
                media.visibility = View.GONE
                supportingText.visibility = View.GONE
                selectButton.visibility = View.GONE
                borderlessSelectButton.visibility = View.VISIBLE
                borderlessSelectButton.setOnClickListener {
                    onItemClickCallback.onItemClicked(answerData)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(answerData: Answer)
    }
}
