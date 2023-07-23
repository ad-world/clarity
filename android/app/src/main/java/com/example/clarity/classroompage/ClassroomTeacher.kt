package com.example.clarity.classroompage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.clarity.IndexActivity
import com.example.clarity.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout

class ClassroomTeacher : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.classroom_teacher)

        val fragmentManager = supportFragmentManager

        val intent = intent
        val classId = intent.getStringExtra("classId")
        val tab = intent.getStringExtra("tasksTab")

        // get the app bar and the tabs
        val appBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        val tabLayout = findViewById<TabLayout>(R.id.teacherTab)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)

        // Create a FragmentPagerAdapter for the ViewPager
        var pagerAdapter: Any
        if (tab != null && tab == "false") {
            pagerAdapter = classId?.let { PagerAdapterTeacher(fragmentManager, it, false) }!!
            // Get the Tab object you want to select (i.e. Tasks tab)
            val tabToSelect = tabLayout.getTabAt(1)
            // Check if the tab exists
            if (tabToSelect != null) {
                tabLayout.selectTab(tabToSelect) // Programmatically set the selected tab
                viewPager.adapter = pagerAdapter
                viewPager.currentItem = tabToSelect.position
            }
        }
        else if (tab != null && tab == "true") {
            pagerAdapter = classId?.let { PagerAdapterTeacher(fragmentManager, it, true) }!!
            // Get the Tab object you want to select (i.e. Tasks tab)
            val tabToSelect = tabLayout.getTabAt(1)
            // Check if the tab exists
            if (tabToSelect != null) {
                tabLayout.selectTab(tabToSelect) // Programmatically set the selected tab
                viewPager.adapter = pagerAdapter
                viewPager.currentItem = tabToSelect.position
            }
        }
        else {
            pagerAdapter = classId?.let { PagerAdapterTeacher(fragmentManager, it, true) }!!
            viewPager.adapter = pagerAdapter
        }

        // handle onclick for back symbol on app bar (it will go back to main classroom page)
        appBar.setNavigationOnClickListener {
            // Replace the entire current fragment with the ClassroomFragment
            val intent = Intent(this, IndexActivity::class.java)
            intent.putExtra("screen", "mainClassroom")
            startActivity(intent)
        }

        // handle onclick for the tabs
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager.currentItem = tab.position
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })
    }
}