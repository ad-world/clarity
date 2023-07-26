package com.example.clarity.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.R
import com.example.clarity.SessionManager
import com.example.clarity.databinding.FragmentFollowersBinding
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.FollowerListResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response


class Followers : Fragment() {

    private val api = ClaritySDK().apiService

    private var _binding: FragmentFollowersBinding? = null

    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

    private var userId = 0
    private val binding get() = _binding!!
    private var followersList: List<Int>? = null
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFollowersBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("HERE")
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            userId = sessionManager.getUserId()
        }
        followersList = getFollowers()
        println(followersList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = followersList?.let { FollowAdapter(it, true, userid = userId) }


    }

    private fun getFollowers(): List<Int>? {
        val response : Response<FollowerListResponse> = runBlocking {
            return@runBlocking api.getFollowers(userId)
        }
        return response.body()?.followers
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}