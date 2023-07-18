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
import java.util.concurrent.atomic.AtomicBoolean

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
            val classes: MutableList<Pair<String, String>> = mutableListOf()

            // toggle switch between student and teacher classes
            var materialSwitch = view.findViewById<MaterialSwitch>(R.id.toggleClass)

            // show default student list when classroom page loads (toggle button is checked
            if (materialSwitch.isChecked) {
                // make api call to get the list of student class names from the backend
                var classesResponse : Response<GetClassroomResponse> = runBlocking {
                    return@runBlocking api.getClassesStudent(uid.toString())
                }
                // confirm if response was received
                if (classesResponse != null && classesResponse.body()?.response == StatusResponse.Success) {
                    val retrievedClassList = classesResponse.body()?.id // list of class names
                    if (retrievedClassList != null) {
                        classes.clear()
                        // loop over each class name item and add it to the classes list
                        for (className in retrievedClassList) {
                            classes.add(Pair(className.name, className.code))
                        }
                    }
                }
                // adding the list classes to the recycler view (with recycler custom ClassAdapter)
                classAdapter = ClassAdapter(classes) { className ->
                    val intent = Intent(requireContext(), Classroom::class.java)
                    intent.putExtra("className", className.first)
                    startActivity(intent)
                }
                recyclerView.adapter = classAdapter
            }

            // update clas list according to toggle switch
            materialSwitch.setOnCheckedChangeListener { switch, isChecked ->
                // if toggle is on, show student classes (i.e. classes user has joined)
                if (isChecked) {
                    switch.text = "Show Your Classes"
                    // make api call to get the list of student class names from the backend
                    var classesResponse : Response<GetClassroomResponse> = runBlocking {
                        return@runBlocking api.getClassesStudent(uid.toString())
                    }
                    // confirm if response was received
                    if (classesResponse != null && classesResponse.body()?.response == StatusResponse.Success) {
                        val retrievedClassList = classesResponse.body()?.id // list of class names
                        if (retrievedClassList != null) {
                            classes.clear()
                            // loop over each class name item and add it to the classes list
                            for (className in retrievedClassList) {
                                classes.add(Pair(className.name, className.code))
                            }
                        }
                    }
                    // adding the list classes to the recycler view (with recycler custom ClassAdapter)
                    classAdapter = ClassAdapter(classes) { className ->
                        val intent = Intent(requireContext(), Classroom::class.java)
                        intent.putExtra("className", className.first)
                        startActivity(intent)
                    }
                    recyclerView.adapter = classAdapter
                }
                // otherwise show teacher's class (i.e. classes that the user has created)
                else {
                    switch.text = "Show Joined Classed"
                    // make api call to get the list of teacher's class names from the backend
                    var classesResponse : Response<GetClassroomResponse> = runBlocking {
                        return@runBlocking api.getClasses(uid.toString())
                    }
                    // confirm if response was received
                    if (classesResponse != null && classesResponse.body()?.response == StatusResponse.Success) {
                        val retrievedClassList = classesResponse.body()?.id // list of class names
                        if (retrievedClassList != null) {
                            classes.clear()
                            // loop over each class name item and add it to the classes list
                            for (className in retrievedClassList) {
                                classes.add(Pair(className.name, className.code))
                            }
                        }
                    }
                    // adding the list classes to the recycler view (with recycler custom ClassAdapter)
                    classAdapter = ClassAdapter(classes) { className ->
                        val intent = Intent(requireContext(), Classroom::class.java)
                        intent.putExtra("className", className.first)
                        startActivity(intent)
                    }
                    recyclerView.adapter = classAdapter
                }
            }

            // creating a listener action for the join class button
            val joinClassButton = view.findViewById<Button>(R.id.btnJoinClass)
            joinClassButton.setOnClickListener {
                // if joining class was a success, showJoinClassDialog should return true-
                val successfullyJoined = runBlocking { showJoinClassDialog() }
                if (successfullyJoined) {
                    println("success here")
                    // set switch to true to show student classes
                    materialSwitch.isChecked = true
                    materialSwitch.text = "Show Your Classes"
                    // make api call to get the list of student class names from the backend
                    var classesResponse : Response<GetClassroomResponse> = runBlocking {
                        return@runBlocking api.getClassesStudent(uid.toString())
                    }
                    // confirm if response was received
                    if (classesResponse != null && classesResponse.body()?.response == StatusResponse.Success) {
                        val retrievedClassList = classesResponse.body()?.id // list of class names
                        if (retrievedClassList != null) {
                            classes.clear()
                            // loop over each class name item and add it to the classes list
                            for (className in retrievedClassList) {
                                classes.add(Pair(className.name, className.code))
                            }
                        }
                    }
                    // adding the list classes to the recycler view (with recycler custom ClassAdapter)
                    classAdapter = ClassAdapter(classes) { className ->
                        val intent = Intent(requireContext(), Classroom::class.java)
                        intent.putExtra("className", className.first)
                        startActivity(intent)
                    }
                    recyclerView.adapter = classAdapter
                }
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
        private fun showJoinClassDialog(): Boolean {
            var success = AtomicBoolean(false)
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
                if (response.body()?.response == StatusResponse.Success &&
                    response.body()?.id == "Classroom Joined") {
                    // add to list of joined classroom
                    println("success set here")
                    success.set(true)
                    // switch screen and switch to the class page
//                    val intent = Intent(activity, Classroom::class.java)
//                    startActivity(intent)
                }
            }
            // do nothing if user cancels the dialog box
            dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                success.set(false)
                dialog.dismiss()
            }
            // create and sow the dialog
            val dialog = dialogBuilder.create()
            dialog.show()
            println("printing return: ${success.get()}")
            return success.get()
        }
}