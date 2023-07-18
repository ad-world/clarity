package com.example.clarity.classroompage

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.IndexActivity
import com.example.clarity.R
import com.example.clarity.SessionManager
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.GetClassroomResponse
import com.example.clarity.sdk.JoinClassroomEntity
import com.example.clarity.sdk.JoinClassroomResponse
import com.example.clarity.sdk.StatusResponse
import com.google.android.material.materialswitch.MaterialSwitch
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class ClassroomFragment : Fragment() {

        // placeholder listview data
        private lateinit var recyclerView: RecyclerView
        private lateinit var classAdapter: ClassAdapter
        private var uid: Int = 0
        private val api = ClaritySDK().apiService
        private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            val view = inflater.inflate(R.layout.fragment_classroom, container, false)

            // connect the classroom list with the frontend design and set its layout
            recyclerView = view.findViewById(R.id.rvClasses)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            //get the userID from the global session manager
            lifecycleScope.launch {
                uid = sessionManager.getUserId()
            }

            // declare a list to show the different classrooms user is currently enrolled in
            val classes = mutableListOf<String>()

            // toggle switch between student and teacher classes
            var materialSwitch = view.findViewById<MaterialSwitch>(R.id.toggleClass)

            // update clas list according to toggle switch
            materialSwitch.setOnCheckedChangeListener { switch, isChecked ->
                // if toggle is on, show student classes (i.e. classes user has joined)
                if (isChecked) {
                    switch.text = "Showing Joined Classes "
                }
                // otherwise show teacher's class (i.e. classes that the user has created)
                else {
                    switch.text = "Showing Your Classes"
                }
            }

            println("good here1")
            // make api call to get the list of class names from the backend
            var classesResponse : Response<GetClassroomResponse> = runBlocking {
                return@runBlocking api.getClasses(uid.toString())
            }
            println("good here2")

            // confirm if response was received
            if (classesResponse != null && classesResponse.body()?.response == StatusResponse.Success) {
                val retrievedClassList = classesResponse.body()?.id // list of class names

                if (retrievedClassList != null) {
                    // loop over each class name item and add it to the classes list
                    for (className in retrievedClassList) {
                        classes.add(className)
                    }
                }
            }

            /*
            TODO
            Replace dummyData with classes list data
             */

            // adding dummy data to the list of classrooms
            val dummyData = arrayOf(
                Pair("ENGL 100", "Instructor: Arshpreet Chabbewal"),
                Pair("ENGL 200", "Instructor: Dhir Patel"),
                Pair("ENGL 300", "Instructor: Talin Sharma"),
                Pair("ENGL 400", "Instructor: Aryaman Dhingra")
            )

            // adding the list classes to the recycler view (with recycler custom ClassAdapter)
            classAdapter = ClassAdapter(dummyData) { className ->
                val intent = Intent(requireContext(), Classroom::class.java)
                intent.putExtra("className", className.first)
                startActivity(intent)
            }
            recyclerView.adapter = classAdapter

            // creating a listener action for the join class button
            val joinClassButton = view.findViewById<Button>(R.id.btnJoinClass)
            joinClassButton.setOnClickListener {
                showJoinClassDialog()
            }

            // creating a listener action for the create class button
            val createClassButton = view.findViewById<Button>(R.id.btnCreateClass)
            createClassButton.setOnClickListener {
                val intent = Intent(activity, Classroom::class.java)
                startActivity(intent)
            }

            return view
        }

        // create a alert dialog when the join class button is pressed. Give the user the option to
        // enter the class code of the class they wish to join.
        private fun showJoinClassDialog() {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setTitle("Join Class")
            val input = EditText(requireContext())
            input.hint = "Enter the classroom code" // add placeholder text to the textfield
            dialogBuilder.setView(input) // add textfeild to the dialog box
            // setting input type to text (allow the user to enter any textual data)
            input.inputType = InputType.TYPE_CLASS_TEXT

            // take action if user joins a class
            dialogBuilder.setPositiveButton("Join") { dialog, _ ->
                val classCode = input.text.toString() // get user's class code input

                // create request body for join classroom api
                var req = JoinClassroomEntity(classCode, uid.toString())
                // get response from join classroom api
                var response : Response<JoinClassroomResponse> = runBlocking {
                    return@runBlocking api.joinClass(req)
                }
                println(response.body())

                // check if the user entered the correct code (i.e. if backend returned the correct response)
                if (response.body()?.response == StatusResponse.Success) {
                    // add to list of joined classroom
                    // TODO

                    // switch screen and switch to the class page
                    val intent = Intent(activity, Classroom::class.java)
                    startActivity(intent)
                }
            }

            dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = dialogBuilder.create()
            dialog.show()
        }
}