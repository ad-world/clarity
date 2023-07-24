package com.example.clarity.sets.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.View.GONE
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
import com.example.clarity.sdk.CreateAttemptResponse
import com.example.clarity.sets.audio.AndroidAudioPlayer
import com.example.clarity.sets.data.Card
import com.example.clarity.sets.data.Set
import com.example.clarity.sets.audio.PrevAndroidAudioRecorder
import com.example.clarity.sets.audio.WavPlayer
import com.example.clarity.sets.audio.WavRecorder
import com.example.clarity.sets.audio.WavUtils
import com.example.clarity.sets.data.SetCategory
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.Locale

class PracticeSetActivity() : AppCompatActivity() {

    // Recorder and Player
    private val recorder by lazy { PrevAndroidAudioRecorder(applicationContext) }
    private var player: TextToSpeech? = null
    // private val player by lazy { AndroidAudioPlayer(applicationContext) }

    // Audio File
    private var audioFile: File? = null
    private var wavFile: File? = null
    private val wavRecorder = WavRecorder(this)
    private val sessionManager: SessionManager by lazy { SessionManager(this) }

    // Toggle to check if we are currently recording
    private var isRecording = false

    // ClaritySDK api for endpoint calls
    private val api = ClaritySDK().apiService

    // Index that stores the current card being displayed
    private var index = 0
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
            Log.d("isWavFile", isWavFile(File(this.filesDir, "audio.wav")).toString())
            Log.d("hasWavData", hasWavData(File(this.filesDir, "audio.wav")).toString())

            // player.playFile(File(this.filesDir, "audio.wav"))
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

                /*
                File(cacheDir, "audio.mp3").also {
                    recorder.start(it)
                    audioFile = it
                }*/

                wavRecorder.startRecording("audio.wav", true)

            // CASE 2: Recording -> Not Recording
            } else {
                // Change UI of button
                iBtnMic.setBackgroundResource(R.drawable.roundcorner)
                iBtnMic.setImageResource(R.drawable.baseline_mic_24)

                // Stop Recording
                // recorder.stop()
                wavRecorder.stopRecording()
                // wavFile = File(cacheDir, "audio.wav")
                // WavUtils.convertMp3ToWav(audioFile!!.absolutePath, wavFile!!.absolutePath)

                // val wavFile = File("")

                // Return Accuracy Score and Display Popup
                val score = getAccuracyScore(File(this.filesDir, "audio.wav"))
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
    private fun getAccuracyScore(wavFile: File): Int {
        // TODO: make this work later
        // val wavFile = convertMp3ToWav(file.absolutePath)
        // val wavFile = file
        // player.playFile(file)
        Log.d("has mic connected: ", packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE).toString())
        val wavPlayer = WavPlayer(applicationContext)
        //wavPlayer.playWavFileFromCache("audio.wav")

        Log.d("Wav File In Score: ", wavFile.toString())
        Log.d("Is Wav File In Score: ", isWavFile(wavFile).toString())
        Log.d("Does Wav file have data: ", hasWavData(wavFile).toString())
        val requestFile = RequestBody.create(MediaType.parse("audio/*"), wavFile)
        val part = MultipartBody.Part.createFormData("audio", wavFile.name, requestFile)
        val response : Response<CreateAttemptResponse> = runBlocking {
            return@runBlocking api.attemptCard(userid, set.cards[index].id, set.id, part)
        }
        if (response.body()?.metadata?.accuracyScore == null) {
            return 0
        }
        // TODO: This currently fails with Error: 400, need to fix this
        Log.d("response: ", "$response")
        Log.d("response accuracy score", response.body()?.metadata?.accuracyScore.toString())
        Log.d("response completeness score", response.body()?.metadata?.completenessScore.toString())
        Log.d("response fluency score", response.body()?.metadata?.fluencyScore.toString())
        Log.d("response mispronunciations score", response.body()?.metadata?.mispronunciations.toString())
        Log.d("response omissions score", response.body()?.metadata?.omissions.toString())
        Log.d("response insertions score", response.body()?.metadata?.insertions.toString())
        Log.d("response pronunciation score", response.body()?.metadata?.pronunciationScore.toString())
        Log.d("response is_complete score", response.body()?.metadata?.is_complete.toString())

        val accuracyMetric = response.body()?.metadata?.accuracyScore?.toInt()!!


        //Log.d("accuracy score: ", "${response.body()!!.metadata!!.accuracyScore}")
        //return response.body()!!.metadata!!.accuracyScore.toInt()
        return accuracyMetric
    }

    // Display popup
    @SuppressLint("SetTextI18n")
    private fun displayMessagePopup(score: Int)  {
        val cvPopUp = findViewById<CardView>(R.id.cvPopUp)
        val tvResultMessage = findViewById<TextView>(R.id.tvResultMessage)

        val difficultyThreshold = 50
        if (score in 0 until difficultyThreshold)  {
            cvPopUp.setCardBackgroundColor(Color.YELLOW)
            tvResultMessage.text = resources.getString(R.string.try_again)
        } else if (score in difficultyThreshold..100) {
            cvPopUp.setCardBackgroundColor(Color.GREEN)
            tvResultMessage.text = resources.getString(R.string.great_job)
        }

        cvPopUp.visibility = View.VISIBLE
    }

    fun isWavFile(wavFile: File): Boolean {
        if (!wavFile.exists() || wavFile == null) {
            return false
        }

        val headerSize = 12 // The WAV header size is 12 bytes
        val buffer = ByteArray(headerSize)

        try {
            val fileInputStream = FileInputStream(wavFile)
            fileInputStream.read(buffer, 0, headerSize)
            fileInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

        // Verify the WAV file header
        val riffHeaderStr = String(buffer, 0, 4, Charset.forName("US-ASCII"))
        val formatHeaderStr = String(buffer, 8, 4, Charset.forName("US-ASCII"))
        Log.d("riffHeader", riffHeaderStr)
        Log.d("formatHeader", formatHeaderStr)

        return riffHeaderStr == "RIFF" && formatHeaderStr == "WAVE"
    }

    fun hasWavData(wavFile: File): Boolean {
        if (!wavFile.exists() || !wavFile.isFile()) {
            return false
        }

        try {
            val fileInputStream = FileInputStream(wavFile)

            // Skip the WAV header (first 44 bytes)
            val headerSize = 44
            fileInputStream.skip(headerSize.toLong())

            // Read the data chunk size (4 bytes, little-endian)
            val dataSizeBuffer = ByteArray(4)
            fileInputStream.read(dataSizeBuffer)

            // Convert the 4 bytes to an integer (little-endian)
            val dataSize = ByteBuffer.wrap(dataSizeBuffer).order(ByteOrder.LITTLE_ENDIAN).int

            fileInputStream.close()

            // If the dataSize is greater than zero, the WAV file contains audio data
            return dataSize > 0
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return false
    }
}