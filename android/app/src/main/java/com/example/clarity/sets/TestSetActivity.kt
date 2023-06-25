package com.example.clarity.sets

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.example.clarity.R
import com.example.clarity.sets.audio.AndroidAudioPlayer
import com.example.clarity.sets.audio.AndroidAudioRecorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

class TestSetActivity() : AppCompatActivity() {

    private val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }

    // TODO: Not sure if they can hear the correct recording after answering?
    //  added this here in case they can
    private val player by lazy {
        AndroidAudioPlayer(applicationContext)
    }

    private var audioFile: File? = null

    private var isRecording = false
    private var recordingCompleted = false

    var index = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        setContentView(R.layout.activity_test_set)

        val intent = intent
        val setId: Int = intent.getIntExtra("setId", 0)
        val userId: Int = intent.getIntExtra("userId", 0)
        // TODO: Backend query to search for set with given userId and setId
        //  for now we use our hard coded sets
        var set = Set(0, "Animals", 4,
                    mutableListOf(Card("Dog", false),
                        Card("Cat", false),
                        Card("Zebra", false),
                        Card("Kangaroo", false)),
                    0, SetCategory.DEFAULT_SET)
        when (setId) {
            0 -> {
                set = Set(0, "Animals", 4,
                    mutableListOf(Card("Dog", false),
                        Card("Cat", false),
                        Card("Zebra", false),
                        Card("Kangaroo", false)),
                    0, SetCategory.DEFAULT_SET)
            }
            1 -> {
                set = Set(1, "Countries", 3,
                    mutableListOf(Card("Canada", false),
                        Card("Russia", false),
                        Card("Japan", false)),
                    0, SetCategory.DOWNLOADED_SET)
            }
            2 -> {
                set = Set(2, "Devices", 5,
                    mutableListOf(Card("Phone", false),
                        Card("Laptop", false),
                        Card("Computer", false),
                        Card("Television", false),
                        Card("Tablet", false)),
                    0, SetCategory.COMMUNITY_SET)
            }
        }

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val iBtnClose = findViewById<ImageButton>(R.id.iBtnClose)
        val iBtnMic = findViewById<ImageButton>(R.id.iBtnMic)
        tvTitle.text = set.title
        iBtnClose.setOnClickListener {
            finish()
        }

        iBtnMic.setOnClickListener {
            if (!recordingCompleted) {
                if (!isRecording) {
                    // TODO: Change UI of button to reflect ongoing recording
                    //  ...
                    File(cacheDir, "audio.mp3").also {
                        recorder.start(it)
                        audioFile = it
                    }
                } else {
                    // TODO Change UI of button to reflect recording stopped
                    //  ...
                    recorder.stop()
                    recordingCompleted = true

                    val score = getAccuracyScore(File(cacheDir, "audio.mp3"))
                    displayMessagePopup(score)
                    index++
                    if (index < set.cards.size) {
                        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                        val tvCompletedCount = findViewById<TextView>(R.id.tvCompletedPhrases)
                        progressBar.progress = index / set.cards.size
                        tvCompletedCount.text = index.toString()
                        set.progress = index
                        loadCard(set.cards[index])
                        recordingCompleted = false
                    } else {
                        // TODO: some completed screen?
                        finish()
                    }
                }
                isRecording = !isRecording
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
    private fun displayMessagePopup(score: Int) = runBlocking {
        val cvPopUp = findViewById<CardView>(R.id.cvPopUp)
        val tvResultMessage = findViewById<TextView>(R.id.tvResultMessage)
        if (score in 0..50)  {
            cvPopUp.setCardBackgroundColor(Color.YELLOW)
            tvResultMessage.text = resources.getString(R.string.try_again)
        } else if (score in 51..100) {
            cvPopUp.setCardBackgroundColor(Color.GREEN)
            tvResultMessage.text = resources.getString(R.string.great_job)
        }

        //TODO: check functionality when we click the close button during this delay
        launch {
            delay(3000L)
            cvPopUp.visibility = INVISIBLE
        }
        cvPopUp.visibility = VISIBLE
    }
}