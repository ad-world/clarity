package com.example.clarity.sets

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.R

class CardAdapter (private val cards: MutableList<Card>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    fun getCards(): MutableList<Card> {
        return cards
    }

    fun addCard() {
        cards.add(Card("", false))
        notifyItemInserted(cards.size - 1)
    }

    private fun deleteCard(position: Int) {
        cards.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.itemView.apply {
            val btnDeleteCard = findViewById<ImageButton>(R.id.iBtnDeleteCard)
            val etCardTitle = findViewById<EditText>(R.id.etCardPhrase)
            btnDeleteCard.setOnClickListener {
                deleteCard(position)
            }
            etCardTitle.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    card.phrase = etCardTitle.text.toString()
                }
            })
        }
    }
}