package com.example.clarity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.example.clarity.databinding.FragmentProfileBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.navigation.NavigationView


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!
    //private lateinit var lineChart: LineChart
    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //navigation

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

        super.onViewCreated(view, savedInstanceState)
//        lineChart = binding.lineChart
//
//
//        //line chart:
//        //lineChart.description.isEnabled = false
//        lineChart.setTouchEnabled(true)
//        lineChart.setPinchZoom(true)



        val totalSavedSets = 100
        var completedSets = 40
        var incompleteSets = 20
        var notStartedSets = 60

        //pie chart:
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
        binding.completedSetsNum.text = completedSets.toString()
    }

    private fun logout() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}