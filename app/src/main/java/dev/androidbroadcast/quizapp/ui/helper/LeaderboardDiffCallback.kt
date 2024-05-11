package dev.androidbroadcast.quizapp.ui.helper

import androidx.recyclerview.widget.DiffUtil
import dev.androidbroadcast.quizapp.domain.model.Leaderboard

class LeaderboardDiffCallback(
    private val oldLeaderboardList: List<Leaderboard>,
    private val newLeaderboardList: List<Leaderboard>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldLeaderboardList.size

    override fun getNewListSize(): Int = newLeaderboardList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldLeaderboardList[oldItemPosition] == newLeaderboardList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldLeaderboardList[oldItemPosition].id == newLeaderboardList[newItemPosition].id
    }
}
