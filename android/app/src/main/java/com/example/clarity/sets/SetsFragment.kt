package com.example.clarity.sets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clarity.databinding.FragmentSetsBinding

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val USER_ID = "userId"

/**
 * A simple [Fragment] subclass.
 * Use the [SetsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetsFragment : Fragment() {

    private var userId: Int? = 0
    private var _binding: FragmentSetsBinding? = null
    private lateinit var setAdapter: SetAdapter
    private lateinit var sets: MutableList<Set>
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun onSetClick(position: Int) {
        val btnTest = binding.btnTest
        val btnPractice = binding.btnPractice
        val btnCancel = binding.iBtnCancel
        val cvStartActivity = binding.cvStartActivity
        val tvPopupSetTitle = binding.tvPopupSetTitle

        if (cvStartActivity.visibility != VISIBLE) {
            tvPopupSetTitle.text = sets[position].title
            cvStartActivity.visibility = VISIBLE

            btnTest.setOnClickListener {
                cvStartActivity.visibility = INVISIBLE
                val intent = Intent(activity, TestSetActivity::class.java)
                intent.putExtra("setId", sets[position].id);
                intent.putExtra("userId", userId);
                startActivity(intent)
            }

            btnPractice.setOnClickListener {
                cvStartActivity.visibility = INVISIBLE
                val intent = Intent(activity, PracticeSetActivity::class.java)
                intent.putExtra("setId", sets[position].id);
                intent.putExtra("userId", userId);
                startActivity(intent)
            }

            btnCancel.setOnClickListener {
                cvStartActivity.visibility = INVISIBLE
            }
        }

        sets = mutableListOf()
        // TODO: Replace following lines with a query for all sets with our userId, and then parse
        //  through the object returned, creating a set data class for each set, and appending it
        //  to the sets array

        // TEST DATA, will be removed when database is connected
        sets.add(Set(0, "Animals", 4,
            mutableListOf(Card("Dog", false),
                Card("Cat", false),
                Card("Zebra", false),
                Card("Kangaroo", false)),
            0, SetCategory.DEFAULT_SET))

        sets.add(Set(1, "Countries", 3,
            mutableListOf(Card("Canada", false),
                Card("Russia", false),
                Card("Japan", false)),
            0, SetCategory.DOWNLOADED_SET))

        sets.add(Set(2, "Devices", 5,
            mutableListOf(Card("Phone", false),
                Card("Laptop", false),
                Card("Computer", false),
                Card("Television", false),
                Card("Tablet", false)),
            0, SetCategory.COMMUNITY_SET))

        setAdapter = SetAdapter(sets) { position -> onSetClick(position) }
        binding.rvSets.adapter = setAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSetsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(USER_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sets = mutableListOf()
        // TODO: Replace following lines with a query for all sets with our userId, and then parse
        //  through the object returned, creating a set data class for each set, and appending it
        //  to the sets array

        // TEST DATA, will be removed when database is connected
        sets.add(Set(0, "Animals", 4,
            mutableListOf(Card(0, "Dog", false),
                Card(1, "Cat", false),
                Card(2, "Zebra", false),
                Card(3, "Kangaroo", false)),
            0, SetCategory.DEFAULT_SET))

        sets.add(Set(1, "Countries", 3,
            mutableListOf(Card(0, "Canada", false),
                Card(1, "Russia", false),
                Card(2, "Japan", false)),
            0, SetCategory.DOWNLOADED_SET))

        sets.add(Set(2, "Devices", 5,
            mutableListOf(Card(0, "Phone", false),
                Card(1, "Laptop", false),
                Card(2, "Computer", false),
                Card(3, "Television", false),
                Card(4, "Tablet", false)),
            0, SetCategory.COMMUNITY_SET))
        Log.d("myTag", "SET SIZE: " + sets.size)
        setAdapter = SetAdapter(sets, userId) { position -> onSetClick(position) }
        binding.rvSets.adapter = setAdapter
        binding.rvSets.layoutManager = LinearLayoutManager(context)
        binding.btnCreateSet.setOnClickListener {
            val intent = Intent(activity, CreateSetActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        // This uses a design pattern lol are we allowed to say that this is one of the ones we implemented

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param userId User ID of user that logged in
         * @return A new instance of fragment SetsFragment.
         */
        @JvmStatic
        fun newInstance(userId: Int) =
            SetsFragment().apply {
                arguments = Bundle().apply {
                    putInt(USER_ID, userId)
                }
            }
    }
}