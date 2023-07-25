package com.example.clarity.classroompage

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

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val student = studentProgress[position]
        println("Position: $position")
        holder.itemView.apply {
            val firstName = findViewById<TextView>(R.id.firstName)
            val lastName = findViewById<TextView>(R.id.lastName)
            val numerator = findViewById<TextView>(R.id.numerator)
            val denominator = findViewById<TextView>(R.id.denominator)

            firstName.text = student.firstName
            lastName.text = student.lastName
            numerator.text = student.completed_count.toString()
            denominator.text = totalCards.toString()
        }
    }
}