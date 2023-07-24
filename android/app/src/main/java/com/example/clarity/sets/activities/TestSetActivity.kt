package com.example.clarity.sets.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.*
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.R
import com.example.clarity.SessionManager
import com.example.clarity.sdk.CreateAttemptResponse
import com.example.clarity.sets.data.Card
import com.example.clarity.sets.data.Set
import com.example.clarity.sets.data.SetCategory
import com.example.clarity.sets.audio.WavRecorder
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.*
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.Locale

class TestSetActivity() : AppCompatActivity() {

    // Recorder and Player
    private var player: TextToSpeech? = null
    private val wavRecorder = WavRecorder(this)

    // Session Manager
    private val sessionManager: SessionManager by lazy { SessionManager(this) }

    // Toggle to check if we are currently recording
    private var isRecording = false

    // Variable to restrict users to one attempt per card
    private var recordingCompleted = false

    // ClaritySDK api for endpoint calls
    private val api = ClaritySDK().apiService

    // Index that stores the current card being displayed
    private var index = 0

    // User and Set
    var userid = 0
    var set: Set = Set(0, "", 0, mutableListOf<Card>(), 0, SetCategory.COMMUNITY_SET)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permission to record
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 0)

        // Set View
        setContentView(R.layout.activity_test_set)

        // Create TTS Object
        player = TextToSpeech(this, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS) {
                player!!.language = Locale.ENGLISH
            } else {
                Log.d("TTS ERROR", it.toString())
            }
        })

        // Get Set that was started
        val intent = intent
        val setJson = intent.getStringExtra("set")
        val gson = Gson()
        set = gson.fromJson(setJson, Set::class.java)

        // Get all view components
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val iBtnClose = findViewById<ImageButton>(R.id.iBtnClose)
        val iBtnMic = findViewById<ImageButton>(R.id.iBtnMic)
        val iBtnSpeaker = findViewById<ImageButton>(R.id.iBtnSpeaker)
        val iBtnNext = findViewById<ImageButton>(R.id.iBtnNext)
        val cvPopUp = findViewById<CardView>(R.id.cvPopUp)
        val cvCompletedScreen = findViewById<CardView>(R.id.cvCompletedScreen)
        val btnReturn = findViewById<Button>(R.id.btnReturn)

        // Get Session Context Variables
        lifecycleScope.launch {
            userid = sessionManager.getUserId()
        }

        // Initialize Title
        tvTitle.text = set.title

        // Handle Close button, which automatically closes test session
        iBtnClose.setOnClickListener {
            finish()
        }

        // Handle Speaker button click
        iBtnSpeaker.setOnClickListener {
            player!!.speak(set.cards[index].phrase, TextToSpeech.QUEUE_ADD, null, null)
        }

        // Handle Mic button click
        iBtnMic.setOnClickListener {
            // Ensure we haven't already recorded an attempt for this card
            if (!recordingCompleted) {
                // CASE 1: Not Recording -> Recording
                if (!isRecording) {
                    // Change UI of button
                    iBtnMic.setBackgroundResource(R.drawable.setclosebutton)
                    iBtnMic.setImageResource(R.drawable.baseline_mic_24_white)

                    // Start recording, while storing recording in file
                    wavRecorder.startRecording("audio.wav", true)

                // CASE 2: Recording -> Not Recording
                } else {
                    // Change UI of button
                    iBtnMic.setBackgroundResource(R.drawable.roundcorner)
                    iBtnMic.setImageResource(R.drawable.baseline_mic_24)

                    // Stop Recording
                    wavRecorder.stopRecording()

                    // Indicate that we have already recorded an attempt for this card
                    recordingCompleted = true
                    iBtnMic.isEnabled = false

                    // Make next button visible
                    iBtnNext.visibility = VISIBLE

                    // Return Accuracy Score and Display Popup
                    val score = getAccuracyScore(File(this.filesDir, "audio.wav"))
                    displayMessagePopup(score)

                    // Increment Index and set Progress
                    index++
                    set.progress = index

                    // Update Progress Components
                    val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                    val tvCompletedCount = findViewById<TextView>(R.id.tvCompletedPhrases)
                    val tvPercentComplete = findViewById<TextView>(R.id.tvPercentComplete)
                    progressBar.progress = (index * 100) / set.cards.size
                    tvCompletedCount.text = "$index Complete"
                    tvPercentComplete.text = "${(index * 100) / set.cards.size} %"
                }

                // Toggle isRecording Value
                isRecording = !isRecording
            }
        }

        // Handle Forward Navigation
        iBtnNext.setOnClickListener {
            iBtnMic.isEnabled = true
            if (index < set.cards.size) {
                iBtnNext.visibility = GONE
                cvPopUp.visibility = GONE
                loadCard(set.cards[index])
                recordingCompleted = false
            } else {
                cvCompletedScreen.visibility = VISIBLE
                iBtnClose.isEnabled = false
                iBtnNext.visibility = GONE
                iBtnMic.isEnabled = false
            }
        }

        // Handle Return after completion
        btnReturn.setOnClickListener {
            cvCompletedScreen.visibility = GONE
            iBtnClose.isEnabled = true
            iBtnMic.isEnabled = true
            finish()
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
    private fun getAccuracyScore(wavFile: File): Int {
        // Convert file to MultipartBody.Part
        val requestFile = RequestBody.create(MediaType.parse("audio/*"), wavFile)
        val part = MultipartBody.Part.createFormData("audio", wavFile.name, requestFile)

        // Make attempt call
        val response: Response<CreateAttemptResponse> = runBlocking {
            return@runBlocking api.attemptCard(userid, set.cards[index].id, set.id, part)
        }

        // Handle failed response case
        if (response.body() == null || response.body()!!.metadata == null) {
            return 0
        }

        // Return with Accuracy Score
        return response.body()?.metadata!!.accuracyScore.toInt()
    }

    // Display popup
    @SuppressLint("SetTextI18n")
    private fun displayMessagePopup(score: Int)  {
        // Get Components
        val cvPopUp = findViewById<CardView>(R.id.cvPopUp)
        val tvResultMessage = findViewById<TextView>(R.id.tvResultMessage)

        // TODO: Make this actually return the threshold later
        // Get Difficulty Threshold
        val difficultyThreshold = 50

        // Set Message Properties based on Difficulty Threshold
        if (score in 0 until difficultyThreshold)  {
            cvPopUp.setCardBackgroundColor(Color.YELLOW)
            tvResultMessage.text = resources.getString(R.string.try_again)
        } else if (score in difficultyThreshold..100) {
            cvPopUp.setCardBackgroundColor(Color.GREEN)
            tvResultMessage.text = resources.getString(R.string.great_job)
        }

        // Make Popup visible
        cvPopUp.visibility = View.VISIBLE
    }
}