package com.example.clarity

import android.os.Bundle
import android.provider.ContactsContract.Profile
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.clarity.databinding.ActivityMainBinding
import com.example.clarity.databinding.IndexActivityBinding
import com.example.clarity.setspage.SetsFragment

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
