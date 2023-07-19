package com.example.clarity.sets

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.example.clarity.sets.audio.AndroidAudioPlayer
import com.example.clarity.sets.audio.AndroidAudioRecorder
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
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
    private val api = ClaritySDK().apiService
    private val sessionManager: SessionManager by lazy { SessionManager(this) }

    var userid = 0
    var index = 0
    var set: Set = Set(0, "", 0, mutableListOf<Card>(), 0, SetCategory.COMMUNITY_SET)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        setContentView(R.layout.activity_test_set)

        val intent = intent
        val setJson = intent.getStringExtra("set")
        val gson = Gson()
        set = gson.fromJson(setJson, Set::class.java)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val iBtnClose = findViewById<ImageButton>(R.id.iBtnClose)
        val iBtnMic = findViewById<ImageButton>(R.id.iBtnMic)
        val iBtnNext = findViewById<ImageButton>(R.id.iBtnNext)
        val cvPopUp = findViewById<CardView>(R.id.cvPopUp)
        val cvCompletedScreen = findViewById<CardView>(R.id.cvCompletedScreen)
        val btnReturn = findViewById<Button>(R.id.btnReturn)

        lifecycleScope.launch {
            userid = sessionManager.getUserId()
        }

        tvTitle.text = set.title
        iBtnClose.setOnClickListener {
            finish()
        }

        iBtnMic.setOnClickListener {
            if (!recordingCompleted) {
                if (!isRecording) {
                    // TODO: Change UI of button to reflect ongoing recording
                    //  ...
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
                    recordingCompleted = true

                    val score = getAccuracyScore(File(cacheDir, "audio.wav"))
                    displayMessagePopup(score)
                    val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                    val tvCompletedCount = findViewById<TextView>(R.id.tvCompletedPhrases)
                    val tvPercentComplete = findViewById<TextView>(R.id.tvPercentComplete)
                    index++
                    progressBar.progress = (index * 100) / set.cards.size
                    tvCompletedCount.text = "$index Complete"
                    tvPercentComplete.text = "${(index * 100) / set.cards.size} %"
                    iBtnNext.visibility = VISIBLE
                    set.progress = index
                    iBtnMic.isEnabled = false
                }
                isRecording = !isRecording
            }
        }

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

        btnReturn.setOnClickListener {
            cvCompletedScreen.visibility = GONE
            iBtnClose.isEnabled = true
            iBtnMic.isEnabled = true
            finish()
        }

        loadCard(set.cards[index])
    }

    private fun loadCard(card: Card) {
        val tvCardPhrase = findViewById<TextView>(R.id.tvCardPhrase)
        tvCardPhrase.text = card.phrase
    }

    private fun getAccuracyScore(file: File): Int {
        // TODO: make this work later
        val inputStream = contentResolver.openInputStream(Uri.fromFile(file))
        val requestFile = RequestBody.create(MediaType.parse("audio.wav"), file)

        val part = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val response : Response<CreateAttemptResponse> = runBlocking {
            return@runBlocking api.attemptCard(set.id, userid, set.cards[index].id, part)
        }
        Log.d("accuracy score: ", "${response.body()!!.metadata!!.accuracyScore}")
        return response.body()!!.metadata!!.accuracyScore.toInt()
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

        cvPopUp.visibility = VISIBLE
        // Log.d("Tag Visibility 1", cvPopUp.visibility.toString())
    }
}