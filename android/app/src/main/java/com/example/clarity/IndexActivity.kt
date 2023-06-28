package com.example.clarity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.clarity.classroompage.ClassroomFragment
import com.example.clarity.databinding.IndexActivityBinding
import com.example.clarity.sets.SetsFragment

// Just created a blank file for the main content

// When the login button is pressed, it should redirect to this file
// The profile, sets, community, and classroom fragments,
// and the logic to switch between them would also rest in this file

class IndexActivity : AppCompatActivity() {

    private lateinit var binding : IndexActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = IndexActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(ProfileFragment())

        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.profile -> replaceFragment(ProfileFragment())
                R.id.sets -> replaceFragment(SetsFragment())
                R.id.community -> replaceFragment(CommunityFragment())
                R.id.classroom -> replaceFragment(ClassroomFragment())
                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}
