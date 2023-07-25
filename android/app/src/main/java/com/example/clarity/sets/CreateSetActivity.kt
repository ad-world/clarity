package com.example.clarity.sets

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
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
import com.example.clarity.sdk.ToggleCardSetRequest
import com.example.clarity.sets.data.PhraseDictionary
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

enum class Visibility {
    PUBLIC, PRIVATE
}
class CreateSetActivity : AppCompatActivity() {
    // CardAdapter that keeps track of all cards being created in recyclerview
    private lateinit var cardAdapter: CardAdapter
    private lateinit var dictionary: PhraseDictionary
    // ClaritySDK api for endpoint calls
    private val api = ClaritySDK().apiService
    // sessionManager to interact with global datastore
    private val sessionManager: SessionManager by lazy { SessionManager(this) }
    private var visibility = Visibility.PRIVATE

    // Title variables
    private var previousText: String = ""
    private var currentText: String = ""
    private var lockTextChanges: Boolean = false

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

    private fun trimText(phrase: String): String {
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
        val etSetTitle = findViewById<AutoCompleteTextView>(R.id.etSetTitle)
        val btnPublic = findViewById<Button>(R.id.btnPublic)
        val btnPrivate = findViewById<Button>(R.id.btnPrivate)
        val btnAddSet = findViewById<Button>(R.id.btnAddSet)
        val btnAddCard = findViewById<Button>(R.id.btnAddCard)
        val iBtnClose = findViewById<ImageButton>(R.id.iBtnClose)

        // Handle Exiting
        iBtnClose.setOnClickListener {
            finish()
        }

        // Handle Visibility Stuff
        btnPublic.backgroundTintList = getColorStateList(R.color.not_selected)
        btnPrivate.backgroundTintList = getColorStateList(R.color.selected)

        btnPublic.setOnClickListener {
            if (visibility == Visibility.PRIVATE) {
                btnPublic.backgroundTintList = getColorStateList(R.color.selected)
                btnPrivate.backgroundTintList = getColorStateList(R.color.not_selected)
                visibility = Visibility.PUBLIC
            }
        }

        btnPrivate.setOnClickListener {
            if (visibility == Visibility.PUBLIC) {
                btnPublic.backgroundTintList = getColorStateList(R.color.not_selected)
                btnPrivate.backgroundTintList = getColorStateList(R.color.selected)
                visibility = Visibility.PRIVATE
            }
        }

        val adapter = WordFilterAdapter(this, dictionary.phrases)
        etSetTitle.setAdapter(adapter)

        etSetTitle.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedSuggestion = parent.getItemAtPosition(position) as String
            lockTextChanges = true
            etSetTitle.editableText.clear()
            etSetTitle.editableText.insert(0, currentText)
            lockTextChanges = false
            // Prevent the AutoCompleteTextView from automatically replacing the text
            val currentText = etSetTitle.text.toString()

            val lastModifiedWordIndex = getLastModifiedWordIndex(currentText, previousText)
            if (lastModifiedWordIndex.first != -1) {
                val editable = etSetTitle.editableText
                editable.replace(lastModifiedWordIndex.first, lastModifiedWordIndex.second, selectedSuggestion)
            }

            etSetTitle.dismissDropDown()
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
                    if (etSetTitle.isPerformingCompletion) {
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
                    if (s != null) {
                        highlightNonDictionaryWords(s, etSetTitle, dictionary.phrases, this)
                    }
                }
            }
        }

        etSetTitle.addTextChangedListener(textWatcher)

        // Handles logic to add set to database
        btnAddSet.setOnClickListener {
            // Get title from editText
            val setTitle = trimText(etSetTitle.text.toString())
            // Check that there are no empty cards
            var allCardsValid = true
            for (card in cardAdapter.getCards()) {
                card.phrase = trimText(card.phrase)
                Log.d("phrase: ", card.phrase)
                if (card.phrase == "" || !allWordsInDictionary(card.phrase)) {
                    Log.d("broken here", card.phrase)
                    allCardsValid = false
                    break
                }
            }
            // Verify that there is a title, there is at least one card, and there are no empty cards
            if (allWordsInDictionary(setTitle) && cardAdapter.getCards().size > 0 && allCardsValid) {
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

                    if (visibility == Visibility.PUBLIC) {
                        runBlocking {
                            return@runBlocking api.toggleCardSetVisibility(ToggleCardSetRequest(newSet.body()!!.set!!.set_id))
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

    private fun getLastModifiedWordIndex(newText: String, oldText: String): Pair<Int, Int> {
        val noCurrentWord = Pair(-1, -1)
        val newTextWords = newText.split(" ")
        val oldTextWords = oldText.split(" ")

        if (kotlin.math.abs(newText.length - oldText.length) > 1) {
            return noCurrentWord
        }

        var index = 0
        var returnIndex = 0
        val min = Integer.min(newTextWords.size, oldTextWords.size)

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