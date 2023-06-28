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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.IndexActivity
import com.example.clarity.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ClassroomFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClassroomFragment : Fragment() {

    // placeholder listview data
    private lateinit var recyclerView: RecyclerView
    private lateinit var classAdapter: ClassAdapter


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_classroom, container, false)

        recyclerView = view.findViewById(R.id.rvClasses)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // adding dummy data to the list of classrooms
        val dummyData = arrayOf(
            Pair("Class 1", "Arsh"),
            Pair("Class 2", "Dhir"),
            Pair("Class 3", "Guneet"),
            Pair("Class 4", "Aryaman"),
            Pair("Class 5", "Saumya"),
            Pair("Class 6", "Talin"),
        )

        // adding the list classes to the recycler view (with recycler custom ClassAdapter)
        classAdapter = ClassAdapter(dummyData) { className ->
            val intent = Intent(requireContext(), Classroom::class.java)
            startActivity(intent)
            // Handle item click event here, e.g., navigate to a separate page
        }
        recyclerView.adapter = classAdapter

        // creating a listener action for the join class button
        val joinClassButton = view.findViewById<Button>(R.id.btnJoinClass)
        joinClassButton.setOnClickListener {
            showJoinClassDialog()
        }

        // creating a listener action for the join class button
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
        dialogBuilder.setView(input)

        // setting input type to text (allow the user to enter any textual data)
        input.inputType = InputType.TYPE_CLASS_TEXT

        dialogBuilder.setPositiveButton("Join") { dialog, _ ->
            val className = input.text.toString()
            // Perform join class operation here (want to switch to the class' page)
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ClassroomFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ClassroomFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}