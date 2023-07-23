package com.example.clarity.classroompage

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class PagerAdapterTeacher(fragmentManager: FragmentManager,
                          private val classId: String,
                          private val tasksFrag: Boolean) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = 2 // Number of tabs

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ClassTeacherAnnouncement(classId)
            1 -> {
                if (tasksFrag) {
                    ClassTeacherTasks(classId)
                } else {
                    ClassroomSelectSets(classId)
                }
                }
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}