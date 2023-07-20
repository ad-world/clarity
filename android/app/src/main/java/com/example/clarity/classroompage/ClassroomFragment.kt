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
import com.example.clarity.sdk.CreateClassroomEntity
import com.example.clarity.sdk.CreateClassroomResponse
import com.example.clarity.sdk.GetClassroomResponse
import com.example.clarity.sdk.GetUserResponse
import com.example.clarity.sdk.JoinClassroomEntity
import com.example.clarity.sdk.JoinClassroomResponse
import com.example.clarity.sdk.StatusResponse
import com.example.clarity.sdk.UserWithId
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

    data class classData(val code: String, val name: String, val teacherID: String, val teacherName: String)

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
        val classes: MutableList<classData> = mutableListOf()
//        val classes: MutableList<Pair<String, String>> = mutableListOf()

        // toggle switch between student and teacher classes
        var materialSwitch = view.findViewById<MaterialSwitch>(R.id.toggleClass)

        // show default student list when classroom page loads (toggle button is checked)
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
                        var userResponse : Response<GetUserResponse> = runBlocking {
                            return@runBlocking api.getUserById(className.teacherID)
                        }
                        var teacherObject : UserWithId
                        var teacherName = ""
                        if (userResponse.body()?.response == StatusResponse.Success) {
                            teacherObject = userResponse.body()?.user!!
                            teacherName = teacherObject.firstname + " " + teacherObject.lastname
                        }
                        val classObject = classData(className.code, className.name, className.teacherID, teacherName)
                        classes.add(classObject)
//                        classes.add(Pair(className.name, className.code))
                    }
                }
            }
            // adding the list classes to the recycler view (with recycler custom ClassAdapter)
            classAdapter = ClassAdapter(classes) { className ->
                val intent = Intent(requireContext(), Classroom::class.java)
                intent.putExtra("classTeacherId", className.teacherID)
                intent.putExtra("classId", className.code)
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
                            var userResponse : Response<GetUserResponse> = runBlocking {
                                return@runBlocking api.getUserById(className.teacherID)
                            }
                            var teacherObject : UserWithId
                            var teacherName = ""
                            if (userResponse.body()?.response == StatusResponse.Success) {
                                teacherObject = userResponse.body()?.user!!
                                teacherName = teacherObject.firstname + " " + teacherObject.lastname
                            }
                            val classObject = classData(className.code, className.name, className.teacherID, teacherName)
                            classes.add(classObject)
//                            classes.add(Pair(className.name, className.code))
                        }
                    }
                }
                // adding the list classes to the recycler view (with recycler custom ClassAdapter)
                classAdapter = ClassAdapter(classes) { className ->
                    val intent = Intent(requireContext(), Classroom::class.java)
                    intent.putExtra("classTeacherId", className.teacherID)
                    intent.putExtra("classId", className.code)
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
                            var userResponse : Response<GetUserResponse> = runBlocking {
                                return@runBlocking api.getUserById(className.teacherID)
                            }
                            var teacherObject : UserWithId
                            var teacherName = ""
                            if (userResponse.body()?.response == StatusResponse.Success) {
                                teacherObject = userResponse.body()?.user!!
                                teacherName = teacherObject.firstname + " " + teacherObject.lastname
                            }
                            val classObject = classData(className.code, className.name, className.teacherID, teacherName)
                            classes.add(classObject)
//                            classes.add(Pair(className.name, className.code))
                        }
                    }
                }
                // adding the list classes to the recycler view (with recycler custom ClassAdapter)
                classAdapter = ClassAdapter(classes) { className ->
                    val intent = Intent(requireContext(), ClassroomTeacher::class.java)
                    intent.putExtra("classTeacherId", className.teacherID)
                    intent.putExtra("classId", className.code)
                    startActivity(intent)
                }
                recyclerView.adapter = classAdapter
            }
        }

        // creating a listener action for the join class button
        val joinClassButton = view.findViewById<Button>(R.id.btnJoinClass)
        joinClassButton.setOnClickListener {
            // if joining class was a success, showJoinClassDialog should return true
            showJoinClassDialog { successfullyJoined ->
                if (successfullyJoined) {
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
                                var userResponse : Response<GetUserResponse> = runBlocking {
                                    return@runBlocking api.getUserById(className.teacherID)
                                }
                                var teacherObject : UserWithId
                                var teacherName = ""
                                if (userResponse.body()?.response == StatusResponse.Success) {
                                    teacherObject = userResponse.body()?.user!!
                                    teacherName = teacherObject.firstname + " " + teacherObject.lastname
                                }
                                val classObject = classData(className.code, className.name, className.teacherID, teacherName)
                                classes.add(classObject)
//                                classes.add(Pair(className.name, className.code))
                            }
                        }
                    }
                    // adding the list classes to the recycler view (with recycler custom ClassAdapter)
                    classAdapter = ClassAdapter(classes) { className ->
                        val intent = Intent(requireContext(), Classroom::class.java)
                        intent.putExtra("classTeacherId", className.teacherID)
                        intent.putExtra("classId", className.code)
                        startActivity(intent)
                    }
                    recyclerView.adapter = classAdapter
                }
            }
        }
        // creating a listener action for the create class button
        val createClassButton = view.findViewById<Button>(R.id.btnCreateClass)
        createClassButton.setOnClickListener {
            var classCode = mutableListOf<String>()
            joinClassDialog(classCode) {classCreated ->
                if (classCreated && classCode.size > 0) {
                    val code = classCode[0] // store class code result in a variable

                    // create a popup to notify the user about the new class code created
                    val dialogBuilder = AlertDialog.Builder(context)
                    dialogBuilder.setTitle("Label Dialog")
                    dialogBuilder.setMessage("Your classroom code is $code")
                    dialogBuilder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val dialog = dialogBuilder.create()
                    dialog.show()

                    // get list of classes that are made by current user (i.e. current user is the teacher)
                    materialSwitch.isChecked = false
                    materialSwitch.text = "Show Joined Classed"
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
                                var userResponse : Response<GetUserResponse> = runBlocking {
                                    return@runBlocking api.getUserById(className.teacherID)
                                }
                                var teacherObject : UserWithId
                                var teacherName = ""
                                if (userResponse.body()?.response == StatusResponse.Success) {
                                    teacherObject = userResponse.body()?.user!!
                                    teacherName = teacherObject.firstname + " " + teacherObject.lastname
                                }
                                val classObject = classData(className.code, className.name, className.teacherID, teacherName)
                                classes.add(classObject)
//                                classes.add(Pair(className.name, className.code))
                            }
                        }
                    }
                    // adding the list classes to the recycler view (with recycler custom ClassAdapter)
                    classAdapter = ClassAdapter(classes) { className ->
                        val intent = Intent(requireContext(), ClassroomTeacher::class.java)
                        intent.putExtra("classTeacherId", className.teacherID)
                        intent.putExtra("classId", className.code)
                        startActivity(intent)
                    }
                    recyclerView.adapter = classAdapter
                }
            }
        }
        return view
    }

    // create a alert dialog when the join class button is pressed. Give the user the option to
    // enter the class code of the class they wish to join.
    private fun showJoinClassDialog(callback: (Boolean) -> Unit) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Join Class")
        val input = EditText(requireContext())
        input.hint = "Enter Classroom Code" // add placeholder text to the textfield
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
            // check if the user entered the correct code (i.e. if backend returned the correct response)
            if (response.body()?.response == StatusResponse.Success &&
                response.body()?.id == "Classroom Joined") {
                // add to list of joined classroom
                callback(true)
            }
        }
        // do nothing if user cancels the dialog box
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            callback(false)
            dialog.dismiss()
        }
        // create and sow the dialog
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    // create a alert dialog when the join class button is pressed. Give the user the option to
    // enter the class code of the class they wish to join.
    private fun joinClassDialog(codeList: MutableList<String>, callback: (Boolean) -> Unit) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Add Class")
        val input = EditText(requireContext())
        input.hint = "Enter Classroom Name" // add placeholder text to the textfield
        dialogBuilder.setView(input) // add textfeild to the dialog box
        // setting input type to text (allow the user to enter any textual data)
        input.inputType = InputType.TYPE_CLASS_TEXT

        // take action if user joins a class
        dialogBuilder.setPositiveButton("Add Class") { dialog, _ ->
            val className = input.text.toString() // get user's class code input
            // create request body for join classroom api
            var req = CreateClassroomEntity(className, uid)
            // get response from join classroom api
            var response : Response<CreateClassroomResponse> = runBlocking {
                return@runBlocking api.createClass(req)
            }
            // check if the user entered the correct code (i.e. if backend returned the correct response)
            if (response.body()?.response == StatusResponse.Success) {
                val classCode = response.body()?.id
                if (classCode != null) {
                    codeList.add(classCode)
                }
                callback(true)
            }
        }
        // do nothing if user cancels the dialog box
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            callback(false)
            dialog.dismiss()
        }
        // create and sow the dialog
        val dialog = dialogBuilder.create()
        dialog.show()
    }
}