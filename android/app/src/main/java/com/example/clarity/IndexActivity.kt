package com.example.clarity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.clarity.classroompage.ClassroomFragment
import com.example.clarity.databinding.IndexActivityBinding
import com.example.clarity.profile.ProfileFragment
import com.example.clarity.sets.SetsFragment

// Just created a blank file for the main content

// When the login button is pressed, it should redirect to this file
// The profile, sets, community, and classroom fragments,
// and the logic to switch between them would also rest in this file

class IndexActivity : AppCompatActivity() {

    private lateinit var binding : IndexActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the intent that started this activity
        val intent = intent
        val screen = intent.getStringExtra("screen")

        binding = IndexActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_index_content_main)

        if (screen == "mainClassroom") {
            binding.bottomNav.selectedItemId = R.id.classroom
            navController.navigate(R.id.ClassroomFragment)
        }

        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.profile -> navController.navigate(R.id.ProfileFragment)
                R.id.sets -> navController.navigate(R.id.SetsFragment)
                R.id.community -> navController.navigate(R.id.Community)
                R.id.classroom -> navController.navigate(R.id.ClassroomFragment)
                else -> {}
            }
            true
        }
    }
}
