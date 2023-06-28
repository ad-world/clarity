package com.example.clarity.classroompage

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.clarity.R

class Classroom : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.classroom)

        val fragmentManager = supportFragmentManager

        // Replace fragment container with Fragment1 initially
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, ClassAnnouncement())
        fragmentTransaction.commit()

        val button1 = findViewById<Button>(R.id.announcementsBttn)
        val button2 = findViewById<Button>(R.id.tasksBttn)

        // Button 1 click listener
        button1.setOnClickListener {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, ClassAnnouncement())
            fragmentTransaction.commit()
        }

        // Button 2 click listener
        button2.setOnClickListener {
            println("button 2 clicked")
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, ClassTask())
            fragmentTransaction.commit()
        }
    }
}