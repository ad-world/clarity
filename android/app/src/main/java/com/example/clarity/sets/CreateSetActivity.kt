package com.example.clarity.sets

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.clarity.R

class CreateSetActivity : AppCompatActivity() {
    private lateinit var setAdapter: SetAdapter
    private lateinit var cardAdapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_set)
        setAdapter = SetAdapter(mutableListOf())
        cardAdapter = CardAdapter(mutableListOf())

        val btnAddSet = findViewById<Button>(R.id.btnAddSet)
        val etSetTitle = findViewById<EditText>(R.id.etSetTitle)

        btnAddSet.setOnClickListener {
            val setTitle = etSetTitle.text.toString()
            if (setTitle.isNotEmpty()) {
                val set = Set(setTitle, 0, cardAdapter.getCards(), 0.0, SetCategory.MYSET)
                setAdapter.addSet(set)
                etSetTitle.text.clear()
            }
        }

        val btnAddCard = findViewById<Button>(R.id.btnAddCard)
        btnAddCard.setOnClickListener {
            cardAdapter.addCard()
        }
    }
}