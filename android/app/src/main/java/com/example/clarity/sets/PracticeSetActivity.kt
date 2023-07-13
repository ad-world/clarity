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

    private val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }

    private val player by lazy {
        AndroidAudioPlayer(applicationContext)
    }

    private var audioFile: File? = null

    private var isRecording = false
    private val api = ClaritySDK().apiService

    var index = 0
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        setContentView(R.layout.activity_practice_set)

        val intent = intent
        val setJson = intent.getStringExtra("set")
        val gson = Gson()
        val set = gson.fromJson(setJson, Set::class.java)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val iBtnClose = findViewById<ImageButton>(R.id.iBtnClose)
        val iBtnMic = findViewById<ImageButton>(R.id.iBtnMic)
        val iBtnNext = findViewById<ImageButton>(R.id.iBtnNext)
        val iBtnPrev = findViewById<ImageButton>(R.id.iBtnPrev)
        val cvPopUp = findViewById<CardView>(R.id.cvPopUp)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvCompletedCount = findViewById<TextView>(R.id.tvCompletedPhrases)
        progressBar.progress = ((index + 1) * 100) / set.cards.size
        tvCompletedCount.text = "Phrase ${index + 1} / ${set.cards.size}"

        tvTitle.text = set.title
        iBtnClose.setOnClickListener {
            finish()
        }

        iBtnMic.setOnClickListener {
            if (!isRecording) {
                // TODO: Change UI of button to reflect ongoing recording
                //  ...
                iBtnNext.isEnabled = false
                iBtnPrev.isEnabled = false
                cvPopUp.visibility = View.GONE
                iBtnMic.setBackgroundResource(R.drawable.setclosebutton)
                iBtnMic.setImageResource(R.drawable.baseline_mic_24_white)
                File(cacheDir, "audio.wav").also {
                    recorder.start(it)
                    audioFile = it
                }
            } else {
                // TODO Change UI of button to reflect recording stopped
                //  ...
                iBtnMic.setBackgroundResource(R.drawable.roundcorner)
                iBtnMic.setImageResource(R.drawable.baseline_mic_24)
                recorder.stop()

                val score = getAccuracyScore(File(cacheDir, "audio.wav"))
                displayMessagePopup(score)
                iBtnNext.isEnabled = true
                iBtnPrev.isEnabled = true
            }
            isRecording = !isRecording
        }

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

        loadCard(set.cards[index])
    }

    private fun loadCard(card: Card) {
        val tvCardPhrase = findViewById<TextView>(R.id.tvCardPhrase)
        tvCardPhrase.text = card.phrase
    }

    private fun getAccuracyScore(file: File): Int {
        // TODO: make this work later
        return 100
    }

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
        // Log.d("Tag Visibility 1", cvPopUp.visibility.toString())
    }
}