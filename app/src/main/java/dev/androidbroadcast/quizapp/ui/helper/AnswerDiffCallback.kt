package dev.androidbroadcast.quizapp.ui.helper

import androidx.recyclerview.widget.DiffUtil
import dev.androidbroadcast.quizapp.domain.model.Answer

class AnswerDiffCallback(
    private val oldAnswerList: List<Answer>,
    private val newAnswerList: List<Answer>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldAnswerList.size

    override fun getNewListSize(): Int = newAnswerList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldAnswerList[oldItemPosition] == newAnswerList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldAnswerList[oldItemPosition].answer == newAnswerList[newItemPosition].answer
    }
}
