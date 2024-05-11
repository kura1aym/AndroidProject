package dev.androidbroadcast.quizapp.ui.helper

import androidx.recyclerview.widget.DiffUtil
import dev.androidbroadcast.quizapp.domain.model.Quiz

class QuizDiffCallback(
    private val oldQuizList: List<Quiz>,
    private val newQuizList: List<Quiz>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldQuizList.size

    override fun getNewListSize(): Int = newQuizList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldQuizList[oldItemPosition] == newQuizList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldQuizList[oldItemPosition].id == newQuizList[newItemPosition].id
    }
}
