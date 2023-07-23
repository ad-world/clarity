package com.example.clarity.classroompage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.clarity.R
import com.example.clarity.databinding.FragmentClassTeacherTaskBinding
import com.example.clarity.sdk.ClaritySDK

/**
 * A simple [Fragment] subclass.
 * Use the [ClassTeacherTasks.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClassTeacherTasks(private val classId: String) : Fragment() {
    private var _binding: FragmentClassTeacherTaskBinding? = null

    private lateinit var announcementAdapter: AnnouncementAdapter
    private val api = ClaritySDK().apiService

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentClassTeacherTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tasks: MutableList<Pair<String, String>> = mutableListOf()

        // update list with current tasks
        // TODO

        // teacher can add announcements to class
        binding.addTask.setOnClickListener {
            val intent = Intent(requireContext(), ClassroomTeacher::class.java)
            intent.putExtra("classId", classId)
            intent.putExtra("tasksTab", "false")
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}