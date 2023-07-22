package com.example.clarity.profile

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
import com.example.clarity.databinding.FragmentSettingsBinding
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.LoginRequest
import com.example.clarity.sdk.LoginResponse
import com.example.clarity.sdk.StatusResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class SettingsFragment : Fragment() {

    private val api = ClaritySDK().apiService

    private var _binding: FragmentSettingsBinding? = null

    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}