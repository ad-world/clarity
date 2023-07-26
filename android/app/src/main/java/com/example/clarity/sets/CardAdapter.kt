package com.example.clarity.sets

import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.R
import com.example.clarity.sets.data.Card
import com.example.clarity.sets.data.PhraseDictionary
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import org.w3c.dom.Text
import java.lang.Integer.min

// This class is used in the CreateSetActivity, and tracks the list of cards being created
class CardAdapter(private val cards: MutableList<Card>, private val dictionary: PhraseDictionary, val hideKeyboard: () -> Unit) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    // Local lock, to ensure there are no race conditions when clicking multiple delete buttons too fast successively
    private var deleteLock = false
    private var counter = 0
    private var previousText: String = ""
    private var currentText: String = ""
    private var lockTextChanges: Boolean = false

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
        // We set the id to a temporary one for now, as we cannot get the true ID until it has been inserted into the database, as a result we put a temporary ID
        cards.add(Card(counter, "", false))
        counter++
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

    private fun getLastModifiedWordLength(currentText: String): Int {
        val words = currentText.trim().split("\\s+".toRegex()).toTypedArray()
        return if (words.isNotEmpty()) words.last().length else 0
    }

    private fun getLastModifiedWordIndex(newText: String, oldText: String): Pair<Int, Int> {
        val noCurrentWord = Pair(-1, -1)
        val newTextWords = newText.split(" ")
        val oldTextWords = oldText.split(" ")

        if (kotlin.math.abs(newText.length - oldText.length) > 1) {
            return noCurrentWord
        }

        var index = 0
        var returnIndex = 0
        val min = min(newTextWords.size, oldTextWords.size)

        while(index < min) {
            if (newTextWords[index] != oldTextWords[index]) {
                return Pair(returnIndex, returnIndex + newTextWords[index].length)
            }
            returnIndex += newTextWords[index].length + 1
            index++
        }

        if (index < newTextWords.size) {
            return Pair(returnIndex, returnIndex + newTextWords[index].length)
        }

        return noCurrentWord
    }


    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        // Current card in view
        val card = cards[position]
        // Apply the following properties
        holder.itemView.apply {
            // Store Components of CardView
            val btnDeleteCard = findViewById<ImageButton>(R.id.iBtnDeleteCard)
            val etCardTitle = findViewById<AutoCompleteTextView>(R.id.etCardPhrase)
            val adapter = WordFilterAdapter(context, dictionary.phrases)
            etCardTitle.setAdapter(adapter)

            etCardTitle.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedSuggestion = parent.getItemAtPosition(position) as String
                lockTextChanges = true
                etCardTitle.editableText.clear()
                etCardTitle.editableText.insert(0, currentText)
                lockTextChanges = false
                // Prevent the AutoCompleteTextView from automatically replacing the text
                val currentText = etCardTitle.text.toString()

                val lastModifiedWordIndex = getLastModifiedWordIndex(currentText, previousText)
                if (lastModifiedWordIndex.first != -1) {
                    val editable = etCardTitle.editableText
                    editable.replace(lastModifiedWordIndex.first, lastModifiedWordIndex.second, selectedSuggestion)
                }

                etCardTitle.dismissDropDown()
            }
            // Text Watcher that keeps track of what is being entered in the textfield, and makes appropriate updates
            // The first two functions are just filler for now
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    if (!lockTextChanges) {
                        if (etCardTitle.isPerformingCompletion) {
                            currentText = s.toString()
                        } else {
                            previousText = s.toString()
                        }
                    }
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
                // After the text has been changed, update card.phrase
                override fun afterTextChanged(s: Editable?) {
                    if (!lockTextChanges) {
                        card.phrase = etCardTitle.text.toString()
                        if (s != null) {
                            highlightNonDictionaryWords(s, etCardTitle, dictionary.phrases, this)
                        }
                    }
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
        }
    }

    private fun isWordInSuggestions(word: String, suggestions: Array<String>): Boolean {
        for (suggestion in suggestions) {
            if (suggestion.equals(word, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    private fun highlightNonDictionaryWords(editable: Editable, autoCompleteTextView: AutoCompleteTextView, suggestions: Array<String>, textWatcher: TextWatcher) {
        val text = editable.toString()
        val words = text.split(" ")

        val spannableBuilder = SpannableStringBuilder()
        var currentStart = 0

        for ((index, word) in words.withIndex()) {
            val isLastWord = index == words.size - 1

            val colorSpan: ForegroundColorSpan = if (isWordInSuggestions(word, suggestions)) {
                ForegroundColorSpan(Color.BLACK) // Use black for words found in suggestions
            } else {
                ForegroundColorSpan(Color.RED)   // Use red for words not found in suggestions
            }

            spannableBuilder.append(word)
            spannableBuilder.setSpan(colorSpan, currentStart, currentStart + word.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            currentStart += word.length

            if (!isLastWord) {
                // Add the space between words
                spannableBuilder.append(" ")
                currentStart += 1
            }
        }

        autoCompleteTextView.removeTextChangedListener(textWatcher)
        editable.replace(0, editable.length, spannableBuilder)
        autoCompleteTextView.addTextChangedListener(textWatcher)
    }
}