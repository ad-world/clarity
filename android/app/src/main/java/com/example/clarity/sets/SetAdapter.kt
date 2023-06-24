package com.example.clarity.sets

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.R

class SetAdapter (
    private val sets: MutableList<Set>
) : RecyclerView.Adapter<SetAdapter.SetViewHolder>() {

    class SetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        return SetViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.set_card,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return sets.size
    }

    fun addSet(set: Set) {
        sets.add(set)
        notifyItemInserted(sets.size - 1)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val curSet = sets[position]
        holder.itemView.apply {
            val setTitle = findViewById<TextView>(R.id.tvSetTitle)
            setTitle.text = "Title: ${curSet.title}\n" +
                    "Count: ${curSet.cardCount}\n" +
                    "Category: ${curSet.category}\n" +
                    "Progress: ${curSet.progress}"

        }
    }
}