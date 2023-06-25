package com.example.clarity.sets

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.R

class SetAdapter (
    private val sets: MutableList<Set>,
    private val onItemClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<SetAdapter.SetViewHolder>() {

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
                R.layout.set_card,
                parent,
                false
            ), onItemClicked
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
        val set = sets[position]
        holder.itemView.apply {
            val setTitle = findViewById<TextView>(R.id.tvSetTitle)

            setTitle.text = "Title: ${set.title}\n" +
                    "Count: ${set.cards.size}\n" +
                    "Category: ${set.category}\n" +
                    "Progress: ${set.progress}" +
                    "Card Phrase 1: "

        }
    }
}