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
import com.example.clarity.sdk.CreateCardEntity
import com.example.clarity.sdk.GetWordsResponse
import com.example.clarity.sets.data.PhraseDictionary
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class CreateSetActivity : AppCompatActivity() {
    // CardAdapter that keeps track of all cards being created in recyclerview
    private lateinit var cardAdapter: CardAdapter
    private lateinit var dictionary: PhraseDictionary
    // ClaritySDK api for endpoint calls
    private val api = ClaritySDK().apiService
    // sessionManager to interact with global datastore
    private val sessionManager: SessionManager by lazy { SessionManager(this) }

    // Temporary function to hide keyboard upon clicking enter
    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(View(this).windowToken, 0)
    }

    private fun allWordsInDictionary(phrase: String): Boolean {
        val phraseWords = phrase.split(" ")
        for (word in phraseWords) {
            if (!dictionary.phrases.contains(word)) {
                Log.d("faled here", word)
                return false
            }
        }
        return true
    }

    private fun trimCard(phrase: String): String {
        val phraseWords = phrase.split("\\s+".toRegex()).toTypedArray()
        Log.d("trimCard words", phraseWords[0])
        if (phraseWords.isEmpty()) return ""
        var newPhraseWords = ""
        for (word in phraseWords) {
            Log.d("trimCard words", word)
            if (word != "") {
                if (newPhraseWords != "") {
                    newPhraseWords += " "
                }
                newPhraseWords += word
            }
        }
        return newPhraseWords
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set View
        setContentView(R.layout.activity_create_set)

        // Get Words and set Dictionary
        val words : Response<GetWordsResponse> = runBlocking {
            return@runBlocking api.getWords()
        }

        val wordList = words.body()!!.result.toTypedArray()

        // Create Card Adapter, and update RecyclerView properties
        dictionary = PhraseDictionary(wordList)
        cardAdapter = CardAdapter(mutableListOf(), dictionary){ hideKeyboard() }
        val rvCards = findViewById<RecyclerView>(R.id.rvCards)
        rvCards.adapter = cardAdapter
        rvCards.layoutManager = LinearLayoutManager(this)

        // Store userid from global datastore
        var userid = -1
        lifecycleScope.launch {
            userid = sessionManager.getUserId()
        }

        // Define interactive components
        val etSetTitle = findViewById<TextInputEditText>(R.id.etSetTitle)
        val btnAddSet = findViewById<Button>(R.id.btnAddSet)
        val btnAddCard = findViewById<Button>(R.id.btnAddCard)

        // Handles logic to add set to database
        btnAddSet.setOnClickListener {
            // Get title from editText
            val setTitle = etSetTitle.text.toString()
            // Check that there are no empty cards
            var allCardsValid = true
            for (card in cardAdapter.getCards()) {
                card.phrase = trimCard(card.phrase)
                Log.d("phrase: ", card.phrase)
                if (card.phrase == "" || !allWordsInDictionary(card.phrase)) {
                    Log.d("broken here", card.phrase)
                    allCardsValid = false
                    break
                }
            }
            // Verify that there is a title, there is at least one card, and there are no empty cards
            if (setTitle.isNotEmpty() && cardAdapter.getCards().size > 0 && allCardsValid) {
                // Create Set
                val newSet : Response<CreateCardSetResponse> = runBlocking {
                    return@runBlocking api.addSet(CreateCardSetEntity(userid, setTitle, ""))
                }

                if (newSet.isSuccessful) {
                    // Create all Cards
                    for (card in cardAdapter.getCards().withIndex()) {
                        runBlocking {
                            return@runBlocking api.createCard(CreateCardEntity(card.value.phrase, card.value.phrase, newSet.body()!!.set!!.set_id))
                        }
                    }
                    // Finish activity, and return to SetsFragment
                    finish()
                } else {
                    // TODO: Display some error message
                }

            }
        }

        // Add new empty card
        btnAddCard.setOnClickListener {
            cardAdapter.addCard()
        }
    }
}