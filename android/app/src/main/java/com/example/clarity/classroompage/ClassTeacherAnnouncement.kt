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
 * Use the [ClassTeacherAnnouncement.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClassTeacherAnnouncement(private val classId: String) : Fragment() {
    private var _binding: FragmentClassTeacherAnnouncementBinding? = null

    private lateinit var announcementAdapter: AnnouncementAdapter
    private val api = ClaritySDK().apiService

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentClassTeacherAnnouncementBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val announcements: MutableList<Pair<String, String>> = mutableListOf()

        // update list with current announcements
        val response: Response<GetAnnouncementsResponse> = runBlocking {
            return@runBlocking api.getAnnouncements(classId)
        }
        if (response.body()?.response  == StatusResponse.Success) {
            val announcementList = response?.body()?.result
            if (announcementList != null) {
                announcements.clear()
                for (announcement in announcementList) {
                    announcements.add(Pair(announcement.text, announcement.description))
                }
            }
        }
        announcementAdapter = AnnouncementAdapter(announcements) {announcement ->
        }
        binding.rvClasses.adapter = announcementAdapter
        binding.rvClasses.layoutManager = LinearLayoutManager(context)

        // teacher can add announcements to class
        binding.addAnnouncement.setOnClickListener {
            var announcementDetail: MutableList<Pair<String, String>> = mutableListOf()
            addAnnouncementsDialog(announcementDetail) {announcementCreated ->
                if (announcementCreated && announcementDetail.size > 0) {
                    val announcementTitle = announcementDetail[0].first
                    val description = announcementDetail[0].second

                    announcements.add(Pair(announcementTitle, description))

                    announcementAdapter = AnnouncementAdapter(announcements) { announcement ->
                    }
                    binding.rvClasses.adapter = announcementAdapter
                    binding.rvClasses.layoutManager = LinearLayoutManager(context)
                }
            }
        }
    }

    // create a alert dialog when the join class button is pressed. Give the user the option to
    // enter the class code of the class they wish to join.
    private fun addAnnouncementsDialog(announccementDetail: MutableList<Pair<String, String>>, callback: (Boolean) -> Unit) {
        // Inflate the custom layout containing two EditText fields
        val customLayout = LayoutInflater.from(requireContext()).inflate(R.layout.create_announcement_input, null)
        val editText1 = customLayout.findViewById<EditText>(R.id.editText1)
        val editText2 = customLayout.findViewById<EditText>(R.id.editText2)

        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(resources.getString(R.string.announcement_title))
                .setView(customLayout) // Set the custom layout with the text field
                .setNeutralButton(resources.getString(R.string.announcement_cancel)) { dialog, which ->
                    callback(false)
                    dialog.cancel()
                }
                .setNegativeButton(resources.getString(R.string.announcement_decline)) { dialog, which ->
                    callback(false)
                    dialog.cancel()
                }
                .setPositiveButton(resources.getString(R.string.announcement_create)) { dialog, which ->
                    // Get the text entered in the text field
                    val titleText = editText1.text.toString()
                    val description = editText2.text.toString()
                    // get current date as string
                    val currDate = getCurrentDateAsString()

                    val announcementReq = CreateAnnouncementEntity(classId, titleText, description, currDate)
                    val resp: Response<AnnouncementResponse> = runBlocking{
                        return@runBlocking api.addAnnouncement(announcementReq)
                    }
                    if (resp.body()?.response == StatusResponse.Success) {
                        announccementDetail.add(Pair(titleText, description))
                        callback(true)
                    }
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun getCurrentDateAsString(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}