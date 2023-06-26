package com.example.clarity.classroompage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.R

class ClassAdapter(
    private val classes: Array<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_class, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val className = classes[position]
        holder.bind(className)
    }

    override fun getItemCount(): Int = classes.size

    inner class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvClassName: TextView = itemView.findViewById(R.id.tvClassName)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val className = classes[position]
                    onItemClick(className)
                }
            }
        }

        fun bind(className: String) {
            tvClassName.text = className
        }
    }
}