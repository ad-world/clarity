package com.example.clarity.community

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.clarity.IndexActivity
import com.example.clarity.R
import com.example.clarity.SessionManager
import com.example.clarity.databinding.FragmentCommunityBinding
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.LoginRequest
import com.example.clarity.sdk.LoginResponse
import com.example.clarity.sdk.StatusResponse
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class CommunityFragment : Fragment() {

    private val api = ClaritySDK().apiService

    private var _binding: FragmentCommunityBinding? = null

    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction()
            .replace(R.id.innerPage, DisplaySetsFragment(0))
            .commit()
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val selectedTab = tab.position
                if(selectedTab == 0) {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.innerPage, DisplaySetsFragment(0))
                        .commit()
                } else if (selectedTab == 1) {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.innerPage, DisplaySetsFragment(1))
                        .commit()
                } else {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.innerPage, DisplaySetsFragment(2))
                        .commit()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}