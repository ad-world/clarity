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
import com.example.clarity.ClaritySDK
import com.example.clarity.GetDataForSetRequest
import com.example.clarity.GetDataForSetResponse
import com.example.clarity.GetSetsResponse
import com.example.clarity.LoginRequest
import com.example.clarity.LoginResponse
import com.example.clarity.databinding.FragmentSetsBinding
import kotlinx.coroutines.runBlocking
import retrofit2.Response

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val USER_ID = "userId"

/**
 * A simple [Fragment] subclass.
 * Use the [SetsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetsFragment : Fragment() {

    private var _binding: FragmentSetsBinding? = null
    private lateinit var setAdapter: SetAdapter
    private lateinit var sets: MutableList<Set>
    // private val api = ClaritySDK().apiService
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun onSetClick(position: Int) {
        val btnTest = binding.btnTest
        val btnPractice = binding.btnPractice
        val btnCancel = binding.iBtnCancel
        val cvStartActivity = binding.cvStartActivity
        val tvPopupSetTitle = binding.tvPopupSetTitle
        val tvNumCards = binding.tvNumCards

        if (cvStartActivity.visibility != VISIBLE) {
            tvPopupSetTitle.text = sets[position].title
            if (sets[position].cards.size == 1) {
                tvNumCards.text = "${sets[position].cards.size} card"
            } else {
                tvNumCards.text = "${sets[position].cards.size} cards"
            }
            cvStartActivity.visibility = VISIBLE

            btnTest.setOnClickListener {
                cvStartActivity.visibility = INVISIBLE
                val intent = Intent(activity, TestSetActivity::class.java)
                intent.putExtra("setId", sets[position].id);
                startActivity(intent)
            }

            btnPractice.setOnClickListener {
                cvStartActivity.visibility = INVISIBLE
                val intent = Intent(activity, PracticeSetActivity::class.java)
                intent.putExtra("setId", sets[position].id);
                startActivity(intent)
            }

            btnCancel.setOnClickListener {
                cvStartActivity.visibility = INVISIBLE
            }
        }
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sets = mutableListOf()
        // TODO: Replace following lines with a query for all sets with our userId, and then parse
        //  through the object returned, creating a set data class for each set, and appending it
        //  to the sets array

/*
        val response : Response<GetSetsResponse> = runBlocking {
            return@runBlocking api.getAllSets()
        }
        println(response.body())

        val size = response.body()!!.sets.size - 1
        for(i in 0..size) {
            val setId = response.body()?.sets?.get(i)!!.toInt()

            val setRes : Response<GetDataForSetResponse> = runBlocking {
                return@runBlocking api.getDataForSet(GetDataForSetRequest(setId))
            }
            val progress = setRes.body()?.data?.get(1)!!.toInt()
            val setTitle = setRes.body()?.data!![2]
            val setCards = setRes.body()?.data?.get(3)
            val cardArray: List<String> = setCards!!.split(",")

            val set = Set(setId, setTitle, 0, mutableListOf<Card>(), progress, SetCategory.CREATED_SET)
            for ((counter, card) in cardArray.withIndex()) {
                set.cards.add(Card(counter, card, false))
            }
            sets.add(set)
        }*/

        // TEST DATA, will be removed when database is connected
        /*sets.add(Set(0, "Animals", 4,
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
            0, SetCategory.COMMUNITY_SET))*/
        Log.d("myTag", "SET SIZE: " + sets.size)
        setAdapter = SetAdapter(sets) { position -> onSetClick(position) }
        binding.rvSets.adapter = setAdapter
        binding.rvSets.layoutManager = LinearLayoutManager(context)
        binding.btnCreateSet.setOnClickListener {
            val intent = Intent(activity, CreateSetActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param userId User ID of user that logged in
         * @return A new instance of fragment SetsFragment.
         */
        @JvmStatic
        fun newInstance() =
            SetsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}