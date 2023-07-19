package com.example.clarity.classroompage

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class PagerAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = 3 // Number of tabs

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ClassAnnouncement()
            1 -> ClassTask()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}