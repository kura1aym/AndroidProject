package dev.androidbroadcast.quizapp.ui.edit.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.androidbroadcast.quizapp.R
import dev.androidbroadcast.quizapp.databinding.CardRowItemBinding
import dev.androidbroadcast.quizapp.domain.model.Answer
import dev.androidbroadcast.quizapp.ui.helper.AnswerDiffCallback

class AnswerAdapter : RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder>() {
    private val listAnswer = ArrayList<Answer>()
    private var deleteButtonStatus = false
    var selectAllStatus = false
    private var selectedItems = intArrayOf()
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var onItemLongClickCallback: OnItemLongClickCallback

    fun setListAnswer(listAnswer: List<Answer>) {
        val diffCallback = AnswerDiffCallback(this.listAnswer, listAnswer)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listAnswer.clear()
        this.listAnswer.addAll(listAnswer)
        diffResult.dispatchUpdatesTo(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCheckbox(deleteButtonStatus: Boolean) {
        this.deleteButtonStatus = deleteButtonStatus
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectAll() {
        this.selectAllStatus = true
        notifyDataSetChanged()
    }

    fun getSelected(): IntArray {
        return selectedItems
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setOnItemLongClickCallback(onItemLongClickCallback: OnItemLongClickCallback) {
        this.onItemLongClickCallback = onItemLongClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val binding = CardRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnswerViewHolder(parent, binding)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.bind(listAnswer[position], position)
        holder.itemView.setOnClickListener {
            if (!deleteButtonStatus) {
                onItemClickCallback.onItemClicked(position)
            } else {
                holder.binding.checkbox.isChecked = !holder.binding.checkbox.isChecked
            }
        }
    }

    override fun getItemCount(): Int {
        return listAnswer.size
    }

    inner class AnswerViewHolder(private val parent: ViewGroup, internal val binding: CardRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(answerData: Answer, adapterPosition: Int) {
            with(binding) {
                media.visibility = View.GONE
                title.text = answerData.answer
                if (answerData.isCorrect) {
                    supportingText.text = parent.resources.getText(R.string.correct_answer)
                } else {
                    supportingText.visibility = View.GONE
                }
                card.setOnLongClickListener {
                    onItemLongClickCallback.onItemLongClicked(binding)
                    true
                }
                if (deleteButtonStatus) {
                    checkboxBar.visibility= View.VISIBLE
                } else {
                    checkboxBar.visibility = View.GONE
                    checkbox.isChecked = false
                }
                if (selectAllStatus) {
                    checkbox.isChecked = true
                }
                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedItems += adapterPosition
                    } else {
                        selectedItems = selectedItems.filter { it != adapterPosition }.toIntArray()
                    }
                }
                selectButton.visibility = View.GONE
                editButton.visibility = View.VISIBLE
                editButton.setOnClickListener {
                    onItemClickCallback.onItemClicked(adapterPosition)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(position: Int)
    }

    interface OnItemLongClickCallback {
        fun onItemLongClicked(cardBinding: CardRowItemBinding)
    }
}
