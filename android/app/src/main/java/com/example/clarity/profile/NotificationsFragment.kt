package com.example.clarity.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.IndexActivity
import com.example.clarity.R
import com.example.clarity.SessionManager
import com.example.clarity.databinding.FragmentNotificationsBinding
import com.example.clarity.sdk.ChangePasswordEntity
import com.example.clarity.sdk.ChangePasswordResponse
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.CreateUserEntity
import com.example.clarity.sdk.CreateUserResponse
import com.example.clarity.sdk.Difficulty
import com.example.clarity.sdk.EditUserEntity
import com.example.clarity.sdk.EditUserResponse
import com.example.clarity.sdk.FollowerListResponse
import com.example.clarity.sdk.GetUnreadResponse
import com.example.clarity.sdk.GetUserResponse
import com.example.clarity.sdk.LoginRequest
import com.example.clarity.sdk.LoginResponse
import com.example.clarity.sdk.Notification
import com.example.clarity.sdk.StatusResponse
import com.example.clarity.sdk.UpdateDifficultyEntity
import com.example.clarity.sdk.UpdateDifficultyResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class NotificationsFragment : Fragment() {

    private val api = ClaritySDK().apiService

    private var _binding: FragmentNotificationsBinding? = null

    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

    private var userId = 0
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            userId = sessionManager.getUserId()
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = getNotifications()?.let { NotificationsAdapter(it, userId) }


    }


    private fun getNotifications(): List<Notification>? {
        val response : Response<GetUnreadResponse> = runBlocking {
            return@runBlocking api.getUnread(userId)
        }
        return response.body()?.messages
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}