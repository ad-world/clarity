package com.example.clarity.classroompage

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.IndexActivity
import com.example.clarity.R
import com.example.clarity.databinding.FragmentClassAnnouncementBinding
import com.example.clarity.databinding.FragmentClassTeacherAnnouncementBinding
import com.example.clarity.databinding.FragmentClassTeacherTaskBinding
import com.example.clarity.databinding.FragmentFirstBinding
import com.example.clarity.sdk.AnnouncementResponse
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.CreateAnnouncementEntity
import com.example.clarity.sdk.CreateClassroomEntity
import com.example.clarity.sdk.CreateClassroomResponse
import com.example.clarity.sdk.GetAnnouncementsResponse
import com.example.clarity.sdk.GetClassroomResponse
import com.example.clarity.sdk.GetUserResponse
import com.example.clarity.sdk.StatusResponse
import com.example.clarity.sdk.UserWithId
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            val intent = Intent(requireContext(), ClassroomCreateTask::class.java)
            intent.putExtra("classId", classId)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}