package com.example.clarity.classroompage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.IndexActivity
import com.example.clarity.R
import com.example.clarity.databinding.FragmentClassAnnouncementBinding
import com.example.clarity.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass.
 * Use the [ClassAnnouncement.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClassAnnouncement : Fragment() {
    private var _binding: FragmentClassAnnouncementBinding? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var announcementAdapter: AnnouncementAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentClassAnnouncementBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dummyData: MutableList<Pair<String, String>> = mutableListOf()
        dummyData.add(Pair("Announcement 1", "Welcome to ENGL100. A course designed to help you improve your speech impediment!"))
        dummyData.add(Pair("Announcement 2", "Reminder: Task 2 is due in 1 day."))

        announcementAdapter = AnnouncementAdapter(dummyData) {announcement ->
        }
        binding.rvClasses.adapter = announcementAdapter
        binding.rvClasses.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}