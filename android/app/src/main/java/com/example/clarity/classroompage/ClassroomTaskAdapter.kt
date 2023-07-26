package com.example.clarity.classroompage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.R
import com.example.clarity.sets.data.Set

class ClassroomTaskAdapter(
    private val sets: MutableList<Set>,
    private val onItemClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<ClassroomTaskAdapter.SetViewHolder>() {

    class SetViewHolder(itemView: View, private val onItemClicked: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            onItemClicked(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        return SetViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.student_task_card,
                parent,
                false
            ), onItemClicked
        )
    }

    override fun getItemCount(): Int {
        return sets.size
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val set = sets[position]
        println("Position: $position")
        holder.itemView.apply {
            val tvSetTitle = findViewById<TextView>(R.id.tvSetTitle)
            val tvCardCount = findViewById<TextView>(R.id.tvCardCount)
            val tvCompletedPhrases = findViewById<TextView>(R.id.tvCompletedPhrases)
            val tvCompletedPercent = findViewById<TextView>(R.id.tvCompletedPercent)
            val progressBarSet = findViewById<ProgressBar>(R.id.progressBarSet)
            val completedCheck = findViewById<ImageView>(R.id.completedCheck)

            tvSetTitle.text = set.title
            if (set.cards.size == 1) {
                tvCardCount.text = "${set.cards.size} Card"
            } else {
                tvCardCount.text = "${set.cards.size} Cards"
            }
            tvCompletedPhrases.text = "${set.progress} Completed"
            tvCompletedPercent.text = "${(set.progress * 100) / set.cards.size} %"
            progressBarSet.progress = (set.progress * 100) / set.cards.size
            if (set.progress == set.cards.size) {
                completedCheck.visibility = View.VISIBLE
            } else {
                completedCheck.visibility = View.GONE
            }
        }
    }
}