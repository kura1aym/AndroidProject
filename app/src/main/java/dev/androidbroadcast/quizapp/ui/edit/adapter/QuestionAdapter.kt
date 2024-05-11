package dev.androidbroadcast.quizapp.ui.edit.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.androidbroadcast.quizapp.R
import dev.androidbroadcast.quizapp.data.model.Question
import dev.androidbroadcast.quizapp.databinding.CardRowItemBinding
import dev.androidbroadcast.quizapp.ui.helper.QuestionDiffCallback


class QuestionAdapter : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {
    private val listQuestion = ArrayList<Question>()
    private var deleteButtonStatus = false
    var selectAllStatus = false
    private var selectedItems = intArrayOf()
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var onItemLongClickCallback: OnItemLongClickCallback

    fun setListQuestion(listQuestion: List<Question>) {
        val diffCallback = QuestionDiffCallback(this.listQuestion, listQuestion)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listQuestion.clear()
        this.listQuestion.addAll(listQuestion)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = CardRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionViewHolder(parent, binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(listQuestion[position], position)
        holder.itemView.setOnClickListener {
            if (!deleteButtonStatus) {
                onItemClickCallback.onItemClicked(position)
            } else {
                holder.binding.checkbox.isChecked = !holder.binding.checkbox.isChecked
            }
        }
    }

    override fun getItemCount(): Int {
        return listQuestion.size
    }

    inner class QuestionViewHolder(private val parent: ViewGroup, internal val binding: CardRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(questionData: Question, adapterPosition: Int) {
            with(binding) {
                media.visibility = View.GONE
                title.text = questionData.question
                supportingText.text = if (questionData.type == 0) {
                    parent.resources.getText(R.string.multiple_choice)
                } else {
                    parent.resources.getText(R.string.short_form)
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
