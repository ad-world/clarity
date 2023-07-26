package com.example.clarity.classroompage

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
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
import com.example.clarity.sdk.PracticeAttemptResponse
import com.example.clarity.sdk.PracticeClassroomAttemptResponse
import com.example.clarity.sets.data.Card
import com.example.clarity.sets.data.Set
import com.example.clarity.sets.audio.WavRecorder
import com.example.clarity.sets.data.SetCategory
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import java.util.Locale

class ClassroomTaskPracticeActivity() : AppCompatActivity() {

    // Recorder and Player
    private var player: TextToSpeech? = null
    private val wavRecorder = WavRecorder(this)

    // Session Manager
    private val sessionManager: SessionManager by lazy { SessionManager(this) }

    // Toggle to check if we are currently recording
    private var isRecording = false

    // ClaritySDK api for endpoint calls
    private val api = ClaritySDK().apiService

    // Index that stores the current card being displayed
    private var index = 0
    private var taskId = -1

    // List that stores missing words
    var failedWords: List<String>? = listOf()

    // User and Set
    var userid = 0
    var set: Set = Set(0, "", 0, mutableListOf<Card>(), 0, SetCategory.COMMUNITY_SET)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create TTS Object
        player = TextToSpeech(this, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS) {
                player!!.language = Locale.ENGLISH
            } else {
                Log.d("TTS ERROR", it.toString())
            }
        })

        taskId = intent.getIntExtra("taskId", -1)
        // Request permission to record
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 0)

        // Set View
        setContentView(R.layout.activity_practice_set)

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
        val iBtnPrev = findViewById<ImageButton>(R.id.iBtnPrev)
        val cvPopUp = findViewById<CardView>(R.id.cvPopUp)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvCompletedCount = findViewById<TextView>(R.id.tvCompletedPhrases)

        // Get Session Context Variables
        lifecycleScope.launch {
            userid = sessionManager.getUserId()
        }

        // Initialize Progress Bar, Completed Count, and Title
        progressBar.progress = ((index + 1) * 100) / set.cards.size
        tvCompletedCount.text = "Phrase ${index + 1} / ${set.cards.size}"
        tvTitle.text = set.title

        // Handle Close button, which automatically closes practice session
        iBtnClose.setOnClickListener {
            finish()
        }

        // Handle Speaker button click
        iBtnSpeaker.setOnClickListener {
            player!!.speak(set.cards[index].phrase, TextToSpeech.QUEUE_ADD, null, null)
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

                // Start recording, while storing recording in file
                wavRecorder.startRecording("audio.wav", true)

            // CASE 2: Recording -> Not Recording
            } else {
                // Change UI of button
                iBtnMic.setBackgroundResource(R.drawable.roundcorner)
                iBtnMic.setImageResource(R.drawable.baseline_mic_24)

                // Stop Recording
                wavRecorder.stopRecording()

                // Return Accuracy Score and Display Popup
                val isComplete = getAccuracyScore(File(this.filesDir, "audio.wav"))
                displayMessagePopup(isComplete)

                // Enable Navigation Buttons
                iBtnNext.isEnabled = true
                iBtnPrev.isEnabled = true
            }

            // Toggle isRecording Value
            isRecording = !isRecording
        }

        // Set invisible if no next card
        if (set.cards.size == 1) {
            iBtnNext.visibility = View.INVISIBLE
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
                    iBtnNext.visibility = INVISIBLE
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
                    iBtnPrev.visibility = INVISIBLE
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
    private fun getAccuracyScore(wavFile: File): Boolean {
        // Convert file to MultipartBody.Part
        val requestFile = RequestBody.create(MediaType.parse("audio/*"), wavFile)
        val part = MultipartBody.Part.createFormData("audio", wavFile.name, requestFile)

        // Make attempt call
        val response: Response<PracticeClassroomAttemptResponse> = runBlocking {
            return@runBlocking api.practiceAttemptClassroomCard(userid, set.cards[index].id, taskId, part)
        }

        Log.d("response", response.toString())
        // Handle failed response case
        if (response.body() == null || response.body()!!.metadata == null) {
            failedWords = null
            return false
        }

        Log.d("response metadata", response.body()!!.metadata.toString())

        val omissions = response.body()!!.metadata?.omissions!!
        val mispronunciations = response.body()!!.metadata?.mispronunciations!!
        failedWords = omissions + mispronunciations
        // Return with isComplete
        return response.body()?.metadata!!.is_complete
    }

    // Display popup
    @SuppressLint("SetTextI18n")
    private fun displayMessagePopup(isComplete: Boolean)  {
        // Get Components
        val cvPopUp = findViewById<CardView>(R.id.cvPopUp)
        val tvResultMessage = findViewById<TextView>(R.id.tvResultMessage)

        // Set Message Properties based on Difficulty Threshold
        if (isComplete)  {
            cvPopUp.backgroundTintList = getColorStateList(R.color.passed)
            tvResultMessage.text = resources.getString(R.string.great_job)
        } else if (failedWords == null) {
            cvPopUp.backgroundTintList = getColorStateList(R.color.failed)
            tvResultMessage.text = "Whoops, No audio was detected, ensure that your microphone is enabled and try again"
        } else {
            cvPopUp.backgroundTintList = getColorStateList(R.color.failed)
            tvResultMessage.text =  resources.getString(R.string.just_a_little_off_keep_practicing)
            if (failedWords!!.isNotEmpty()) {
                tvResultMessage.text = tvResultMessage.text as String + "\n The following words weren't picked up: " + getFailedWords(
                    failedWords!!
                )
            }
        }

        // Make Popup visible
        cvPopUp.visibility = View.VISIBLE
    }

    private fun getFailedWords(failedWords: List<String>): String {
        var result = ""
        for (i in failedWords.indices) {
            result += failedWords[i]
            if (i != failedWords.size - 1) {
                result += ", "
            }
        }

        return result
    }
}