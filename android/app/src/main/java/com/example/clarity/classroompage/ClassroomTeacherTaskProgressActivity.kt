package com.example.clarity.classroompage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.R
import com.example.clarity.SessionManager
import com.example.clarity.sdk.CreateAttemptResponse
import com.example.clarity.sdk.CreateClassroomAttemptResponse
import com.example.clarity.sdk.GetClassroomTaskProgressResponse
import com.example.clarity.sdk.StatusResponse
import com.example.clarity.sdk.StudentProgress
import com.example.clarity.sets.data.Card
import com.example.clarity.sets.data.Set
import com.example.clarity.sets.data.SetCategory
import com.example.clarity.sets.audio.WavRecorder
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Part
import retrofit2.http.Query
import java.io.*
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.Locale

class ClassroomTeacherTaskProgressActivity() : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var classProgressAdapter: ClassProgressAdapter

    // ClaritySDK api for endpoint calls
    private val api = ClaritySDK().apiService

    // global variable to hold total number of cards
    private var totalCards: Int = 0

    // list storing students progress
    private lateinit var studentProgressList: MutableList<StudentProgress>

    var set: Set = Set(0, "", 0, mutableListOf<Card>(), 0, SetCategory.COMMUNITY_SET)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classroom_task)

        val intent = intent
        val taskId = intent.getIntExtra("taskId", -1)

        // connect the student list with the frontend design and set its layout
        recyclerView = findViewById(R.id.rvTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // get other components of the page
        val closeBtn = findViewById<ImageButton>(R.id.iBtnClose)

        closeBtn.setOnClickListener {
            finish()
        }

        studentProgressList = mutableListOf()

//        val classroomTaskResponseObject = GetClassroomTaskProgressResponse(
//            response = StatusResponse.Success,
//            task_id = 0,
//            card_count = 0,
//            studentProgress = listOf(),
//            message = "hello"
//        )

        // make api call to get the progress for current task
        if (taskId != -1) {
            val taskProgressResponse: Response<GetClassroomTaskProgressResponse> = runBlocking {
                return@runBlocking api.getTaskProgress(taskId)
            }
            // validate response
            if (taskProgressResponse.body()?.response == StatusResponse.Success) {
                Log.i("progres", taskProgressResponse.body().toString())
                totalCards = taskProgressResponse.body()!!.card_count!!
                val studentList = taskProgressResponse.body()!!.studentProgress
                if (studentList != null) {
                    studentProgressList.clear()
                    for (student in studentList) {
                        studentProgressList.add(student)
                    }
                    // adding the list classes to the recycler view (with recycler custom ClassAdapter)
                    classProgressAdapter = ClassProgressAdapter(totalCards, studentProgressList) { student ->
                    }
                    recyclerView.adapter = classProgressAdapter
                }
            }
        } else {
            println("task id is -1")
        }
    }
}