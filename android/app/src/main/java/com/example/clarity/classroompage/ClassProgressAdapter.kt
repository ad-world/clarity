package com.example.clarity.classroompage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.R
import com.example.clarity.sdk.StudentProgress
import com.example.clarity.sets.data.Set

class ClassProgressAdapter(
    private val totalCards: Int,
    private val studentProgress: MutableList<StudentProgress>,
    private val onItemClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<ClassProgressAdapter.SetViewHolder>() {

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
                R.layout.student_card,
                parent,
                false
            ), onItemClicked
        )
    }

    override fun getItemCount(): Int {
        return studentProgress.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val student = studentProgress[position]
        println("Position: $position")
        holder.itemView.apply {
            val firstName = findViewById<TextView>(R.id.firstName)
            val lastName = findViewById<TextView>(R.id.lastName)
            val cardsCompleted = findViewById<TextView>(R.id.cardsCompleted)

            firstName.text = student.firstName
            lastName.text = student.lastName
            cardsCompleted.text = "${student.completed_count} / $totalCards"
        }
    }
}