package com.example.clarity.sets

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.R
import com.google.android.material.textfield.TextInputEditText


class CardAdapter(private val cards: MutableList<Card>, val hideKeyboard: () -> Unit) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    private var counter = 0
    private var deleteLock = false

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
        Log.d("card add", counter.toString())
        cards.add(Card(counter, "", false))
        Log.d("card phrase", cards[cards.size - 1].phrase)
        counter++
        notifyItemInserted(cards.size - 1)
    }

    private fun deleteCard(cardId: Int) {
        var position = 0
        for (i in 0..cards.size) {
            if (cards[i].id == cardId) {
                Log.d("phrase delete", cards[i].phrase)
                position = i
                break
            }
        }
        Log.d("delete ID Position", position.toString())
        cards.removeAll { card -> card.id == cardId }
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.itemView.apply {
            val btnDeleteCard = findViewById<ImageButton>(R.id.iBtnDeleteCard)
            val etCardTitle = findViewById<TextInputEditText>(R.id.etCardPhrase)
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    /* if (s!!.isNotEmpty() && s[start].toString() == "\n") {
                        etCardTitle.text?.removeRange(start..start)
                        etCardTitle.clearFocus()
                        hideKeyboard()
                    }*/
                }

                override fun afterTextChanged(s: Editable?) {
                    Log.d("changed ID", card.id.toString())
                    card.phrase = etCardTitle.text.toString()
                }
            }

            btnDeleteCard.setOnClickListener {
                Log.d("delete lock check", deleteLock.toString())
                if (!deleteLock) {
                    Log.d("delete lock pre set", deleteLock.toString())
                    deleteLock = true
                    Log.d("delete lock post set", deleteLock.toString())
                    Log.d("delete ID", card.id.toString())
                    Log.d("position", position.toString())
                    etCardTitle.text?.clear()
                    etCardTitle.removeTextChangedListener(textWatcher)
                    deleteCard(card.id)
                    postDelayed({
                        deleteLock = false
                    }, 300)
                }
            }
            etCardTitle.addTextChangedListener(textWatcher)
            //TODO: This is acc such a pain, can't get it to close
            /*
            etCardTitle.setOnKeyListener (View.OnKeyListener{ view, i, keyEvent ->
                Log.d("here", "hi")
                if (i == KeyEvent.KEYCODE_ENTER) {
                    val imm: InputMethodManager? = getSystemService(context, InputMethodManager::class.java)
                    imm?.hideSoftInputFromWindow(windowToken, 0)
                    etCardTitle.isFocusable = false
                    etCardTitle.isFocusableInTouchMode = false
                    return@OnKeyListener true
                }
                false
            })*/
        }
    }
}