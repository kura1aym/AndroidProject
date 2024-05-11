package dev.androidbroadcast.quizapp.ui.play.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.androidbroadcast.quizapp.databinding.CheckboxRowItemBinding
import dev.androidbroadcast.quizapp.domain.model.Answer

class MultipleChoiceVariantAdapter :
    RecyclerView.Adapter<MultipleChoiceVariantAdapter.MultipleChoiceVariantViewHolder>() {
    private val listAnswer = ArrayList<Answer>()
    private var selectedItems = intArrayOf()

    fun setListAnswer(listAnswer: List<Answer>) {
        this.listAnswer.addAll(listAnswer)
    }

    fun getSelected(): IntArray {
        return selectedItems
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MultipleChoiceVariantViewHolder {
        val binding =
            CheckboxRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MultipleChoiceVariantViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MultipleChoiceVariantViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.bind(listAnswer[position])
    }

    override fun getItemCount(): Int {
        return listAnswer.size
    }

    inner class MultipleChoiceVariantViewHolder(internal val binding:CheckboxRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(answerData: Answer) {
            with(binding.checkbox) {
                text = answerData.answer
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedItems += adapterPosition
                    } else {
                        selectedItems = selectedItems.filter { it != adapterPosition }.toIntArray()
                    }
                }
            }
        }
    }
}
