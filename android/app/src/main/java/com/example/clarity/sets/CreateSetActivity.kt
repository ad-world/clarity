package com.example.clarity.sets

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.CreateCardSetEntity
import com.example.clarity.sdk.CreateCardSetResponse
import com.example.clarity.R
import com.example.clarity.SessionManager
import com.example.clarity.sdk.AddCardToSetRequest
import com.example.clarity.sdk.AddCardToSetResponse
import com.example.clarity.sdk.CreateCardEntity
import com.example.clarity.sdk.CreateCardResponse
import com.example.clarity.sdk.CreateUserEntity
import com.example.clarity.sdk.CreateUserResponse
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class CreateSetActivity : AppCompatActivity() {
    private lateinit var cardAdapter: CardAdapter
    private val api = ClaritySDK().apiService
    private val sessionManager: SessionManager by lazy { SessionManager(this) }

    private var userid = 0

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

        lifecycleScope.launch {
            userid = sessionManager.getUserId()
        }

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
                var title = setTitle
                var type = ""

                val response : Response<CreateCardSetResponse> = runBlocking {
                    return@runBlocking api.addSet(CreateCardSetEntity(userid, title, type))
                }

                for (card in cardAdapter.getCards().withIndex()) {
                    runBlocking {
                        return@runBlocking api.createCard(CreateCardEntity(card.value.phrase, card.value.phrase, response.body()!!.set!!.set_id))
                    }
                }
                finish()
            }
        }

        val btnAddCard = findViewById<Button>(R.id.btnAddCard)
        btnAddCard.setOnClickListener {
            cardAdapter.addCard()
        }
    }
}