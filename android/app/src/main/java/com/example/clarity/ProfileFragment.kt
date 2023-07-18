package com.example.clarity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.clarity.databinding.FragmentProfileBinding
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.GetUserResponse
import com.example.clarity.sdk.UserWithId
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response


class ProfileFragment : Fragment() {

    private val api = ClaritySDK().apiService
    private var _binding: FragmentProfileBinding? = null

    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

    private val binding get() = _binding!!
    //private lateinit var lineChart: LineChart
    private lateinit var pieChart: PieChart
    private var username: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //navigation
        lifecycleScope.launch {
            username = sessionManager.getUserName()
        }

        val dropdownView: ImageView = binding.dropdown


        dropdownView.setOnClickListener {
            val menu = PopupMenu(view.context, dropdownView)
            menu.inflate(R.menu.profile_menu) // Replace with your menu resource file
            menu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_settings -> {
                        true
                    }
                    R.id.logout -> {
                        logout()
                        true
                    }
                    // Add more menu item cases as needed
                    else -> false
                }
            }
            menu.show()
        }

        //do followers and following after


        val numFollowers = 1

        binding.followers.text = numFollowers.toString() + " Followers"
        val followers = binding.followers


        followers.setOnHoverListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_HOVER_ENTER -> {
                    followers.setTextColor(Color.GRAY)
                }
                MotionEvent.ACTION_HOVER_EXIT -> {
                    followers.setTextColor(Color.BLACK)
                }
            }
            false
        }

        binding.followers.setOnClickListener {
            //findNavController().navigate(R.id.)
        }
        val numFollowing = 1

        binding.following.text = numFollowing.toString() + " Following"
        val following = binding.following

        following.setOnHoverListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_HOVER_ENTER -> {
                    following.setTextColor(Color.GRAY)
                }
                MotionEvent.ACTION_HOVER_EXIT -> {
                    following.setTextColor(Color.BLACK)
                }
            }
            false
        }
        binding.following.setOnClickListener {
            //findNavController().navigate(R.id.)
        }


        val response : Response<GetUserResponse> = runBlocking {
            return@runBlocking api.getUser(username)
        }
        println(response.body())

        val user = response.body()?.user


        //streaks
        val streak = user?.login_streak
        binding.streak.text = "\uD83D\uDD25" + streak.toString() + " Day Streak"

        super.onViewCreated(view, savedInstanceState)

        var selectedTab = 0
        sets()

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectedTab = tab.position
                if(selectedTab == 0) {
                    binding.lineChart.clear()
                    binding.pieChart.clear()
                    sets()
                } else {
                    binding.lineChart.clear()
                    binding.pieChart.clear()
                    cards()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

    }

    private fun logout() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }
    private fun cards() {
        binding.progress.text = "Cards Progress"
        binding.completedText.text = "Completed Cards"
        val totalSavedCards = 100
        var completedCards = 40
        var incompleteCards = 20
        var notStartedCards = 60
        binding.completedNum.text = completedCards.toString()
        //get cards


        val attempts = listOf(
            Entry(0f, 10f),
            Entry(1f, 20f),
            Entry(2f, 15f),
            Entry(3f, 30f),
        )

        val lineDataSet = LineDataSet(attempts, "Data Set")
        val lineData = LineData(lineDataSet)
        val chart = binding.lineChart
        chart.data = lineData
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.axisRight.isEnabled = false
        chart.axisLeft.setDrawGridLines(false)
        chart.xAxis.setDrawGridLines(false)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.animateX(1000)
        chart.invalidate()

        pieChart = binding.pieChart
        val entries = mutableListOf<PieEntry>()
        entries.add(PieEntry(completedCards.toFloat() / totalSavedCards, "Completed"))
        entries.add(PieEntry((incompleteCards.toFloat() / totalSavedCards), "Incomplete"))
        entries.add(PieEntry((notStartedCards.toFloat() / totalSavedCards), "Not Started"))
        val dataSet = PieDataSet(entries, "")
        val colourList = listOf(
            Color.parseColor("#C6C6C6"), //light grey
            Color.parseColor("#74ABFF"), //light blue
            Color.parseColor("#3546D9") //blue
        )
        dataSet.colors = colourList
        val data = PieData(dataSet)
        pieChart.setUsePercentValues(true)
        pieChart.setDrawEntryLabels(false)
        pieChart.description.isEnabled = false
        pieChart.data = data
        pieChart.invalidate()


        //number of sets completed
        binding.completedNum.text = completedCards.toString()
    }
    private fun sets(){
        binding.completedText.text = "Completed Sets"
        binding.progress.text = "Saved Sets Progress"


        val totalSavedSets = 100
        var completedSets = 40
        var incompleteSets = 20
        var notStartedSets = 60
        binding.completedNum.text = completedSets.toString()

        val attempts = listOf(
            Entry(0f, 10f),
            Entry(1f, 20f),
            Entry(2f, 15f),
            Entry(3f, 30f),
        )

        val lineDataSet = LineDataSet(attempts, "Data Set")
        val lineData = LineData(lineDataSet)
        val chart = binding.lineChart
        chart.data = lineData
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.axisRight.isEnabled = false
        chart.axisLeft.setDrawGridLines(false)
        chart.xAxis.setDrawGridLines(false)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.animateX(1000)
        chart.invalidate()

        pieChart = binding.pieChart
        val entries = mutableListOf<PieEntry>()
        entries.add(PieEntry(completedSets.toFloat() / totalSavedSets, "Completed"))
        entries.add(PieEntry((incompleteSets.toFloat() / totalSavedSets), "Incomplete"))
        entries.add(PieEntry((notStartedSets.toFloat() / totalSavedSets), "Not Started"))
        val dataSet = PieDataSet(entries, "")
        val colourList = listOf(
            Color.parseColor("#C6C6C6"), //light grey
            Color.parseColor("#74ABFF"), //light blue
            Color.parseColor("#3546D9") //blue
        )
        dataSet.colors = colourList
        val data = PieData(dataSet)
        pieChart.setUsePercentValues(true)
        pieChart.setDrawEntryLabels(false)
        pieChart.description.isEnabled = false
        pieChart.data = data
        pieChart.invalidate()


        //number of sets completed
        binding.completedNum.text = completedSets.toString()
        //get sets
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}