package com.example.clarity.sets

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.R
import com.example.clarity.sets.audio.AndroidAudioPlayer
import com.example.clarity.sets.audio.AndroidAudioRecorder
import com.google.gson.Gson
import java.io.File

class PracticeSetActivity() : AppCompatActivity() {

    // Recorder and Player
    private val recorder by lazy { AndroidAudioRecorder(applicationContext) }
    private val player by lazy { AndroidAudioPlayer(applicationContext) }

    // Audio File
    private var audioFile: File? = null

    // Toggle to check if we are currently recording
    private var isRecording = false

    // ClaritySDK api for endpoint calls
    private val api = ClaritySDK().apiService

    // Index that stores the current card being displayed
    var index = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permission to record
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 0)

        // Set View
        setContentView(R.layout.activity_practice_set)

        // Get Set that was started
        val intent = intent
        val setJson = intent.getStringExtra("set")
        val gson = Gson()
        val set = gson.fromJson(setJson, Set::class.java)

        // Get all view components
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val iBtnClose = findViewById<ImageButton>(R.id.iBtnClose)
        val iBtnMic = findViewById<ImageButton>(R.id.iBtnMic)
        val iBtnNext = findViewById<ImageButton>(R.id.iBtnNext)
        val iBtnPrev = findViewById<ImageButton>(R.id.iBtnPrev)
        val cvPopUp = findViewById<CardView>(R.id.cvPopUp)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvCompletedCount = findViewById<TextView>(R.id.tvCompletedPhrases)

        // Initialize Progress Bar, Completed Count, and Title
        progressBar.progress = ((index + 1) * 100) / set.cards.size
        tvCompletedCount.text = "Phrase ${index + 1} / ${set.cards.size}"
        tvTitle.text = set.title

        // Handle Close button, which automatically closes practice session
        iBtnClose.setOnClickListener {
            finish()
        }

        // Handle Mic button click
        iBtnMic.setOnClickListener {
            // CASE 1: Not Recording -> Recording
            if (!isRecording) {
                // Disable Navigation Buttons
                iBtnNext.isEnabled = false
                iBtnPrev.isEnabled = false

                // Ensure Result Pop up is Gone
                cvPopUp.visibility = View.GONE

                // Change UI of button
                iBtnMic.setBackgroundResource(R.drawable.setclosebutton)
                iBtnMic.setImageResource(R.drawable.baseline_mic_24_white)

                // Create file, and start recording, while storing recording in file
                File(cacheDir, "audio.wav").also {
                    recorder.start(it)
                    audioFile = it
                }

            // CASE 2: Recording -> Not Recording
            } else {
                // Change UI of button
                iBtnMic.setBackgroundResource(R.drawable.roundcorner)
                iBtnMic.setImageResource(R.drawable.baseline_mic_24)

                // Stop Recording
                recorder.stop()

                // Return Accuracy Score and Display Popup
                val score = getAccuracyScore(File(cacheDir, "audio.wav"))
                displayMessagePopup(score)

                // Enable Navigation Buttons
                iBtnNext.isEnabled = true
                iBtnPrev.isEnabled = true
            }

            // Toggle isRecording Value
            isRecording = !isRecording
        }

        // Handle Forward Navigation
        iBtnNext.setOnClickListener {
            if (index < set.cards.size - 1) {
                index++
                iBtnMic.isEnabled = true
                progressBar.progress = ((index + 1) * 100) / set.cards.size
                tvCompletedCount.text = "Phrase ${index + 1} / ${set.cards.size}"
                cvPopUp.visibility = View.GONE
                loadCard(set.cards[index])
                if(index == set.cards.size - 1) {
                    iBtnNext.visibility = GONE
                }
                iBtnPrev.visibility = VISIBLE
            }
        }

        // Handle Backward Navigation
        iBtnPrev.setOnClickListener {
            if (index > 0) {
                index--
                iBtnMic.isEnabled = true
                progressBar.progress = ((index + 1) * 100) / set.cards.size
                tvCompletedCount.text = "Phrase ${index + 1} / ${set.cards.size} "
                cvPopUp.visibility = View.GONE
                loadCard(set.cards[index])
                if(index == 0) {
                    iBtnPrev.visibility = GONE
                }
                iBtnNext.visibility = VISIBLE
            }
        }

        // Load Initial Card
        loadCard(set.cards[index])
    }

    // Handles setting the UI for the current card
    private fun loadCard(card: Card) {
        val tvCardPhrase = findViewById<TextView>(R.id.tvCardPhrase)
        tvCardPhrase.text = card.phrase
    }

    // Returns accuracy score
    private fun getAccuracyScore(file: File): Int {
        // TODO: make this work later
        return 100
    }

    // Display popup
    @SuppressLint("SetTextI18n")
    private fun displayMessagePopup(score: Int)  {
        val cvPopUp = findViewById<CardView>(R.id.cvPopUp)
        val tvResultMessage = findViewById<TextView>(R.id.tvResultMessage)
        if (score in 0..50)  {
            cvPopUp.setCardBackgroundColor(Color.YELLOW)
            tvResultMessage.text = resources.getString(R.string.try_again)
        } else if (score in 51..100) {
            cvPopUp.setCardBackgroundColor(Color.GREEN)
            tvResultMessage.text = resources.getString(R.string.great_job)
        }

        cvPopUp.visibility = View.VISIBLE
    }
}