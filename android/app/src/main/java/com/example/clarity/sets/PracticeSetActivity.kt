package com.example.clarity.sets

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clarity.R

class PracticeSetActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_set)

        val intent = intent
        val setId: Int = intent.getIntExtra("setId", 0)
        val userId: Int = intent.getIntExtra("userId", 0)
        // TODO: Backend query to search for set with given userId and setId
        //  for now we use our hard coded sets
        var set: Set
        when (setId) {
            0 -> {
                set = Set(0, "Animals", 4,
                    mutableListOf(Card("Dog", false),
                        Card("Cat", false),
                        Card("Zebra", false),
                        Card("Kangaroo", false)),
                    0, SetCategory.DEFAULT_SET)
            }
            1 -> {
                set = Set(1, "Countries", 3,
                    mutableListOf(Card("Canada", false),
                        Card("Russia", false),
                        Card("Japan", false)),
                    0, SetCategory.DOWNLOADED_SET)
            }
            2 -> {
                set = Set(2, "Devices", 5,
                    mutableListOf(Card("Phone", false),
                        Card("Laptop", false),
                        Card("Computer", false),
                        Card("Television", false),
                        Card("Tablet", false)),
                    0, SetCategory.COMMUNITY_SET)
            }
        }
    }

    fun loadCard() {

    }
}