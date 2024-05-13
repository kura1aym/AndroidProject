package dev.androidbroadcast.quizapp.ui.play.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.androidbroadcast.quizapp.data.model.SidebarData
import dev.androidbroadcast.quizapp.databinding.SidebarCardRowItemBinding

class SidebarAdapter :
    RecyclerView.Adapter<SidebarAdapter.SidebarViewHolder>() {
    private val listSidebarData = ArrayList<SidebarData>()

    fun setListSidebarData(listSidebarData: List<SidebarData>) {
        this.listSidebarData.addAll(listSidebarData)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SidebarViewHolder {
        val binding =
            SidebarCardRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SidebarViewHolder(binding, parent)
    }

    override fun onBindViewHolder(holder: SidebarViewHolder, position: Int) {
        holder.bind(listSidebarData[position], position)
    }

    override fun getItemCount(): Int {
        return listSidebarData.size
    }

    inner class SidebarViewHolder(
        internal val binding: SidebarCardRowItemBinding,
        val parent: ViewGroup
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(questionData: SidebarData, position: Int) {
            with(binding) {
                if (questionData.isActive) {
                    with(cardActive) {
                        visibility = View.VISIBLE
                        (position+1).toString().also { titleActive.text = it }
                    }
                } else if (questionData.isAnswered) {
                    with(cardAnswered) {
                        visibility = View.VISIBLE
                        (position+1).toString().also { titleAnswered.text = it }
                    }
                } else {
                    with(cardUnanswered) {
                        visibility = View.VISIBLE
                        (position+1).toString().also { titleUnanswered.text = it }
                    }
                }
            }
        }
    }
}
