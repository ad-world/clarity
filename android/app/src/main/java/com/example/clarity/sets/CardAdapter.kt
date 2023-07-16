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

// This class is used in the CreateSetActivity, and tracks the list of cards being created
class CardAdapter(private val cards: MutableList<Card>, val hideKeyboard: () -> Unit) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    // Local lock, to ensure there are no race conditions when clicking multiple delete buttons too fast successively
    private var deleteLock = false

    // Creates a card view in the recycler view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card,
                parent,
                false
            )
        )
    }

    // Returns all cards
    override fun getItemCount(): Int {
        return cards.size
    }

    // Returns all cards currently being created
    fun getCards(): MutableList<Card> {
        return cards
    }

    // Adds a new card to the list of cards being created
    fun addCard() {
        // We set the id to 0 for now, as we cannot get the true ID until it has been inserted into the database, as a result we put a temporary ID
        cards.add(Card(0, "", false))
        notifyItemInserted(cards.size - 1)
    }

    // Deletes card from list of cards being created
    private fun deleteCard(cardId: Int) {
        var position = 0
        for (i in 0..cards.size) {
            if (cards[i].id == cardId) {
                position = i
                break
            }
        }
        cards.removeAll { card -> card.id == cardId }
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        // Current card in view
        val card = cards[position]
        // Apply the following properties
        holder.itemView.apply {
            // Store Components of CardView
            val btnDeleteCard = findViewById<ImageButton>(R.id.iBtnDeleteCard)
            val etCardTitle = findViewById<TextInputEditText>(R.id.etCardPhrase)

            // Text Watcher that keeps track of what is being entered in the textfield, and makes appropriate updates
            // The first two functions are just filler for now
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
                // After the text has been changed, update card.phrase
                override fun afterTextChanged(s: Editable?) {
                    card.phrase = etCardTitle.text.toString()
                }
            }

            // Handle functionality for delete button
            btnDeleteCard.setOnClickListener {
                if (!deleteLock) {
                    deleteLock = true
                    etCardTitle.text?.clear()
                    etCardTitle.removeTextChangedListener(textWatcher)
                    deleteCard(card.id)
                    postDelayed({
                        deleteLock = false
                    }, 300)
                }
            }

            // add textWatcher defined above to etCardTitle
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