package dev.androidbroadcast.quizapp.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.androidbroadcast.quizapp.data.model.Quiz
import dev.androidbroadcast.quizapp.databinding.CardRowItemBinding
import dev.androidbroadcast.quizapp.ui.helper.QuizDiffCallback

class EditAdapter : RecyclerView.Adapter<EditAdapter.TopicViewHolder>() {
    private val listQuiz = ArrayList<Quiz>()
    private var deleteButtonStatus = false
    var selectAllStatus = false
    private var selectedItems = intArrayOf()
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var onItemLongClickCallback: OnItemLongClickCallback

    fun setListQuiz(listQuiz: List<Quiz>) {
        val diffCallback = QuizDiffCallback(this.listQuiz, listQuiz)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listQuiz.clear()
        this.listQuiz.addAll(listQuiz)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding = CardRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.bind(listQuiz[position], position)
        holder.itemView.setOnClickListener {
            if (!deleteButtonStatus) {
                onItemClickCallback.onItemClicked(listQuiz[position])
            } else {
                holder.binding.checkbox.isChecked = !holder.binding.checkbox.isChecked
            }
        }
    }

    override fun getItemCount(): Int {
        return listQuiz.size
    }

    inner class TopicViewHolder(internal val binding: CardRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(quizData: Quiz, adapterPosition: Int) {
            with(binding) {
                media.visibility = View.GONE
                title.text = quizData.title
                supportingText.text = quizData.desc
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
                    onItemClickCallback.onItemClicked(quizData)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(quizData: Quiz)
    }

    interface OnItemLongClickCallback {
        fun onItemLongClicked(cardBinding: CardRowItemBinding)
    }
}
