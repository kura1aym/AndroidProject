package dev.androidbroadcast.quizapp.ui.helper

import androidx.recyclerview.widget.DiffUtil
import dev.androidbroadcast.quizapp.data.model.Question

class QuestionDiffCallback(
    private val oldQuestionList: List<Question>,
    private val newQuestionList: List<Question>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldQuestionList.size

    override fun getNewListSize(): Int = newQuestionList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldQuestionList[oldItemPosition] == newQuestionList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldQuestionList[oldItemPosition].question == newQuestionList[newItemPosition].question
    }
}
