package com.example.clarity.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.clarity.SessionManager
import com.example.clarity.databinding.FragmentProfileBinding
import com.example.clarity.sdk.ClaritySDK
import com.github.mikephil.charting.charts.PieChart
import kotlinx.coroutines.launch

class CommunityFragment : Fragment() {

    private val api = ClaritySDK().apiService
    private var _binding: FragmentProfileBinding? = null

    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

    private val binding get() = _binding!!

    //private lateinit var lineChart: LineChart
    private lateinit var pieChart: PieChart


    private var username: String = ""
    private var userId: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //navigation
        lifecycleScope.launch {
            username = sessionManager.getUserName()
            userId = sessionManager.getUserId()
        }


    }
}