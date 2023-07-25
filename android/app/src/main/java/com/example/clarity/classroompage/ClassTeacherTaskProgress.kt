package com.example.clarity.classroompage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clarity.R
import com.example.clarity.databinding.FragmentClassTeacherTaskBinding
import com.example.clarity.databinding.FragmentClassTeacherTaskProgressBinding
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.GetTasksEntity
import com.example.clarity.sdk.GetTasksResponse
import com.example.clarity.sdk.StatusResponse
import com.example.clarity.sdk.Task
import kotlinx.coroutines.runBlocking
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Use the [ClassTeacherTaskProgress.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClassTeacherTaskProgress(private val classId: String) : Fragment() {
    private var _binding: FragmentClassTeacherTaskProgressBinding? = null

    private lateinit var classTeacherTaskAdapter: ClassroomTeacherTaskAdapter
    private val api = ClaritySDK().apiService

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentClassTeacherTaskProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tasks: MutableList<Task> = mutableListOf()

        // update list with current tasks
        val getTaskEntity = GetTasksEntity(classId)
        // call api to get the list of tasks
        val response: Response<GetTasksResponse> = runBlocking {
            return@runBlocking api.getTasks(getTaskEntity)
        }
        // check for valid response status
        if (response.body()?.response == StatusResponse.Success) {
            val listOfTasks = response.body()?.id
            if (listOfTasks != null) {
                tasks.clear()
                for (task in listOfTasks) {
                    tasks.add(task)
                }
                classTeacherTaskAdapter = ClassroomTeacherTaskAdapter(tasks) {task ->
                }
                binding.rvClasses.adapter = classTeacherTaskAdapter
                binding.rvClasses.layoutManager = LinearLayoutManager(context)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}