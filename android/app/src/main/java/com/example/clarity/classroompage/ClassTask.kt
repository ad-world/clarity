package com.example.clarity.classroompage

import com.example.clarity.SessionManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clarity.databinding.FragmentClassTaskBinding
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.GetCardsInSetRequest
import com.example.clarity.sdk.GetCardsInSetResponse
import com.example.clarity.sdk.GetSetsByUsernameResponse
import com.example.clarity.sdk.GetTasksEntity
import com.example.clarity.sdk.GetTasksResponse
import com.example.clarity.sdk.StatusResponse
import com.example.clarity.sdk.Task
import com.example.clarity.sets.SetAdapter
import com.example.clarity.sets.activities.PracticeSetActivity
import com.example.clarity.sets.activities.TestSetActivity
import com.example.clarity.sets.data.Card
import com.example.clarity.sets.data.Set
import com.example.clarity.sets.data.SetCategory
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import kotlinx.coroutines.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val USER_ID = "userId"

/**
 * A simple [Fragment] subclass.
 * Use the [SetsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClassTask(private val classId: String, private val classTeacherId: String) : Fragment() {
    // Fragment binding
    private var _binding: FragmentClassTaskBinding? = null

    // Set Adapter for list of sets
    private lateinit var classTaskAdapter: ClassroomTaskAdapter
    private lateinit var sets: MutableList<com.example.clarity.sets.data.Set>
    private lateinit var tasks: MutableList<Task>

    // Variables to store username and userid
    private lateinit var username: String
    private var userid: Int = 0

    // ClaritySDK api for endpoint calls
    private val api = ClaritySDK().apiService

    // sessionManager to interact with global datastore
    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun onSetClick(position: Int) {
        // Get all Variables
        val btnTest = binding.btnTest
        val btnPractice = binding.btnPractice
        val btnCancel = binding.iBtnCancel
        val cvStartActivity = binding.cvStartActivity
        val tvPopupSetTitle = binding.tvPopupSetTitle
        val tvNumCards = binding.tvNumCards

        val taskId = tasks[position].taskId

        // Make sure that cvStartActivity is not already up, otherwise the button won't work
        if (cvStartActivity.visibility != VISIBLE) {
            //
            tvPopupSetTitle.text = sets[position].title
            if (sets[position].cards.size == 1) {
                tvNumCards.text = "${sets[position].cards.size} card"
            } else {
                tvNumCards.text = "${sets[position].cards.size} cards"
            }
            cvStartActivity.visibility = VISIBLE

            btnTest.setOnClickListener {
                cvStartActivity.visibility = INVISIBLE
                val set = sets[position]
                val gson = Gson()
                val setJson = gson.toJson(set)
                val intent = Intent(activity, ClassroomTaskTestActivity::class.java).apply {
                    putExtra("set", setJson)
                    putExtra("taskId", taskId)
                }
                startActivity(intent)
            }

            btnPractice.setOnClickListener {
                cvStartActivity.visibility = INVISIBLE
                val set = sets[position]
                val gson = Gson()
                val setJson = gson.toJson(set)
                val intent = Intent(activity, ClassroomTaskPracticeActivity::class.java).apply {
                    putExtra("set", setJson)
                    putExtra("taskId", taskId)
                }
                startActivity(intent)
            }

            btnCancel.setOnClickListener {
                cvStartActivity.visibility = INVISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentClassTaskBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillLayout()
    }

    override fun onResume() {
        super.onResume()
        fillLayout()
    }

    private fun fillLayout() {
        lifecycleScope.launch {
            username = sessionManager.getUserName()
            userid = sessionManager.getUserId()
            Log.d("Username is: ", username)
        }
        sets = mutableListOf()
        tasks = mutableListOf()
        // TODO: Replace following lines with a query for all sets with our userId, and then parse
        //  through the object returned, creating a set data class for each set, and appending it
        //  to the sets array

        // get a list of tasks associated with this class

        // set up request body
        val getTaskEntity = GetTasksEntity(classId)
        // make api call to fetch list of tasks
        val taskResponse: Response<GetTasksResponse> = runBlocking {
            return@runBlocking api.getTasks(getTaskEntity)
        }
        // validate response received
        if (taskResponse.body()?.response == StatusResponse.Success) {
            val taskList = taskResponse.body()?.id
            val mutableTaskList = taskList?.toMutableList()
            if (mutableTaskList != null) {
                tasks.clear()
                sets.clear()
                tasks = mutableTaskList
            }
            val tasksSize = tasks.size
            for (i in 0 until tasksSize) {
                val singleTask = tasks.get(i)
                val setId = singleTask.setId
                val setTitle = singleTask.name
                val progress = 0
                val set = Set(setId, setTitle, classTeacherId.toInt(), mutableListOf<Card>(), progress, SetCategory.CREATED_SET)

                // get the cards corresponding to the task/set
                val cards : Response<GetCardsInSetResponse> = runBlocking {
                    return@runBlocking api.getCards(GetCardsInSetRequest(setId))
                }
                // validate fetching card response
                if (cards.isSuccessful) {
                    for (card in cards.body()!!.cards) {
                        set.cards.add(Card(card.card_id, card.phrase, false))
                    }
                }
                sets.add(set)
            }
        }
        // call setAdapter to update the list view and display the tasks/sets
        classTaskAdapter = ClassroomTaskAdapter(sets) { position -> onSetClick(position) }
        binding.rvSets.adapter = classTaskAdapter
        binding.rvSets.layoutManager = LinearLayoutManager(context)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param userId User ID of user that logged in
         * @return A new instance of fragment SetsFragment.
         */
        @JvmStatic
        fun newInstance(classId: String, classTeacherId: String) =
            ClassTask(classId, classTeacherId).apply {
                arguments = Bundle().apply {
                }
            }
    }
}