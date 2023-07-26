package com.example.clarity.classroompage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clarity.SessionManager
import com.example.clarity.databinding.FragmentClassroomSetsBinding
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.CreateTaskEntity
import com.example.clarity.sdk.CreateTaskResponse
import com.example.clarity.sdk.Difficulty
import com.example.clarity.sdk.GetCardsInSetRequest
import com.example.clarity.sdk.GetCardsInSetResponse
import com.example.clarity.sdk.GetSetsByUsernameResponse
import com.example.clarity.sdk.GetTasksEntity
import com.example.clarity.sdk.GetTasksResponse
import com.example.clarity.sdk.GetUserSetProgressRequest
import com.example.clarity.sdk.GetUserSetProgressResponse
import com.example.clarity.sdk.StatusResponse
import com.example.clarity.sdk.Task
import com.example.clarity.sets.data.Card
import com.example.clarity.sets.activities.PracticeSetActivity
import com.example.clarity.sets.data.Set
import com.example.clarity.sets.SetAdapter
import com.example.clarity.sets.data.SetCategory
import com.example.clarity.sets.activities.TestSetActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [ClassroomSelectSets.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClassroomSelectSets(private val classId: String) : Fragment() {
    // Fragment binding
    private var _binding: FragmentClassroomSetsBinding? = null

    // Set Adapter for list of sets
    private lateinit var setAdapter: SetAdapter
    private lateinit var sets: MutableList<Set>

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

    private fun getCurrentDatePlusTwoDays(): String {
        // Get the current date
        val currentDate = Calendar.getInstance().time

        // Add two days to the current date
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_YEAR, 2)
        val futureDate = calendar.time

        // Format the date as a string
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(futureDate)
    }

    private fun onSetClick(position: Int) {
        println("selected set here")
        // get the set clicked
        val selectedSet = sets[position]

        var selectedSetsId: MutableList<Int> = mutableListOf()

        // update list with current tasks
        val getTaskEntity = GetTasksEntity(classId, userid)
        // call api to get the list of tasks
        val response: Response<GetTasksResponse> = runBlocking {
            return@runBlocking api.getTasks(getTaskEntity)
        }
        // check for valid response status
        if (response.body()?.response == StatusResponse.Success) {
            val listOfTasks = response.body()?.id
            if (listOfTasks != null) {
                for (task in listOfTasks) {
                    selectedSetsId.add(task.setId)
                }
            }
        }

        // ensure we only add the same type of set once for a task
        if (selectedSet.id !in selectedSetsId) {
            // create fields for making new task
            val setIdForTask = selectedSet.id.toString()
            val setNameForTask = selectedSet.title
            val taskDescription = "English Practice"
            val taskDueDate = getCurrentDatePlusTwoDays() // make due date 2 days after current date
            val taskDifficulty = Difficulty.Medium

            // creating new task entity for request body
            val taskEntity = CreateTaskEntity(classId, setIdForTask, setNameForTask, taskDescription, taskDueDate, taskDifficulty)
            // make the api call to create new task
            val response: Response<CreateTaskResponse> = runBlocking {
                return@runBlocking api.createTask(taskEntity)
            }
            if (response.body()?.response == StatusResponse.Success) {
                println("added new task")
                Toast.makeText(requireContext(), "Task Added!", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Toast.makeText(requireContext(), "Task was already added.", Toast.LENGTH_SHORT).show();
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentClassroomSetsBinding.inflate(inflater, container, false)

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
        // TODO: Replace following lines with a query for all sets with our userId, and then parse
        //  through the object returned, creating a set data class for each set, and appending it
        //  to the sets array


        val response : Response<GetSetsByUsernameResponse> = runBlocking {
            return@runBlocking api.getSetsByUsername(username)
        }
        println(response.body())

        if (response.isSuccessful) {
            val size = response.body()!!.data.size

            for(i in 0 until size) {
                val setData = response.body()?.data?.get(i)!!
                val setId = setData.set_id
                val setTitle = setData.title
                // get set progress
                val progressResponse : Response<GetUserSetProgressResponse> = runBlocking {
                    return@runBlocking api.getSetProgress(GetUserSetProgressRequest(setId, userid))
                }
                // assign set progress to a variable
                val progress = progressResponse.body()!!.numCompletedCards
                // create set object
                val set = Set(
                    setId,
                    setTitle,
                    userid,
                    mutableListOf<Card>(),
                    progress,
                    SetCategory.CREATED_SET
                )
                // get all cards in the set
                val cards : Response<GetCardsInSetResponse> = runBlocking {
                    return@runBlocking api.getCards(GetCardsInSetRequest(setId))
                }
                if (cards.isSuccessful) {
                    for (card in cards.body()!!.cards) {
                        set.cards.add(Card(card.card_id, card.phrase, false))
                    }
                }
                // add set object to list of sets
                sets.add(set)
            }
        }
        // add sets to the ui list
        setAdapter = SetAdapter(sets) { position -> onSetClick(position) }
        binding.rvSets.adapter = setAdapter
        binding.rvSets.layoutManager = LinearLayoutManager(context)

        // handle on click for going back to view the tasks page
        binding.backToTasks.setOnClickListener {
            val intent = Intent(requireContext(), ClassroomTeacher::class.java)
            intent.putExtra("classId", classId)
            intent.putExtra("tasksTab", "true")
            startActivity(intent)
        }
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
        fun newInstance(classId: String) =
            ClassroomSelectSets(classId).apply {
                arguments = Bundle().apply {
                    putString("classId", classId)
                }
            }
    }
}