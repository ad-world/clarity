package com.example.clarity.sets

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.R
import com.google.android.material.textfield.TextInputEditText

class CreateSetActivity : AppCompatActivity() {
    private lateinit var cardAdapter: CardAdapter

    fun HideKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(View(this).windowToken, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_set)
        cardAdapter = CardAdapter(mutableListOf()){ HideKeyboard() }
        val rvCards = findViewById<RecyclerView>(R.id.rvCards)
        rvCards.adapter = cardAdapter
        rvCards.layoutManager = LinearLayoutManager(this)

        val btnAddSet = findViewById<Button>(R.id.btnAddSet)
        val etSetTitle = findViewById<TextInputEditText>(R.id.etSetTitle)

        btnAddSet.setOnClickListener {
            val setTitle = etSetTitle.text.toString()
            var allCardsFull = true
            for (card in cardAdapter.getCards()) {
                if (card.phrase == "") {
                    allCardsFull = false
                    break
                }
            }
            if (setTitle.isNotEmpty() && cardAdapter.getCards().size > 0 && allCardsFull) {
                // TODO: Compute a new set id, probably take largest current ID for user and increment
                // val setId = 0
                // val set = Set(setId, setTitle, cardAdapter.getCards().size, cardAdapter.getCards(), 0, SetCategory.CREATED_SET)
                // TODO: Upload SET to Database
                finish()
            }
        }

        val btnAddCard = findViewById<Button>(R.id.btnAddCard)
        btnAddCard.setOnClickListener {
            cardAdapter.addCard()
        }
    }
}