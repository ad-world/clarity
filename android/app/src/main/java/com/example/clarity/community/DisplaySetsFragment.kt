package com.example.clarity.community

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clarity.SessionManager
import com.example.clarity.databinding.FragmentDisplaySetsBinding
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.GetCardSetsForFollowingRequest
import com.example.clarity.sdk.GetCardSetsForFollowingResponse
import com.example.clarity.sdk.GetCardsInSetRequest
import com.example.clarity.sdk.GetCardsInSetResponse
import com.example.clarity.sdk.GetSetsByUsernameResponse
import com.example.clarity.sdk.getPublicCardSetsOrderedByLikesResponse
import com.example.clarity.sets.CreateSetActivity
import com.example.clarity.sets.SetAdapter
import com.example.clarity.sets.activities.PracticeSetActivity
import com.example.clarity.sets.data.Card
import com.example.clarity.sets.data.Set
import com.example.clarity.sets.data.SetCategory
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class DisplaySetsFragment(page: Int) : Fragment() {
    // Fragment binding
    private var _binding: FragmentDisplaySetsBinding? = null
    private var page = page

    // Set Adapter for list of sets
    private lateinit var setAdapter: CommunitySetAdapter
    private lateinit var sets: MutableList<Set>

    // Variables to store username and userid
    private lateinit var username: String
    private var userid: Int = 0

    // ClaritySDK api for endpoint calls
    private val api = ClaritySDK().apiService

    // sessionManager to interact with global datastore
    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun onSetClick(position: Int) {
        // Get all Variables
        val btnPractice = binding.btnPractice
        val btnCancel = binding.iBtnCancel
        val cvStartActivity = binding.cvStartActivity
        val tvPopupSetTitle = binding.tvPopupSetTitle
        val tvNumCards = binding.tvNumCards

        // Make sure that cvStartActivity is not already up, otherwise the button won't work
        if (cvStartActivity.visibility != View.VISIBLE) {
            //
            tvPopupSetTitle.text = sets[position].title
            if (sets[position].cards.size == 1) {
                tvNumCards.text = "${sets[position].cards.size} card"
            } else {
                tvNumCards.text = "${sets[position].cards.size} cards"
            }
            cvStartActivity.visibility = View.VISIBLE


            btnPractice.setOnClickListener {
                cvStartActivity.visibility = View.INVISIBLE
                val set = sets[position]
                val gson = Gson()
                val setJson = gson.toJson(set)
                val intent = Intent(activity, PracticeSetActivity::class.java).apply {
                    putExtra("set", setJson)
                }
                startActivity(intent)
            }

            btnCancel.setOnClickListener {
                cvStartActivity.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDisplaySetsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillLayout()
    }

    override fun onResume() {
        super.onResume()
        fillLayout()
    }

    private fun fillLayout() {
        lifecycleScope.launch {
            username = sessionManager.getUserName()
            userid = sessionManager.getUserId()
            Log.d("Username is: ", username)
        }
        sets = mutableListOf()


//        if(page == 0) {
//            //page is the recommended
//
//
//        } else
        if (page == 1 || page == 0) { //dont know where the recommended sets api is
            //page is the those that you are following

            val response : Response<GetCardSetsForFollowingResponse> = runBlocking {
                return@runBlocking api.getCardSetsForFollowing(GetCardSetsForFollowingRequest(userid))
            }

            if (response.isSuccessful) {
                val size = response.body()!!.sets.size

                for(i in 0 until size) {
                    val setData = response.body()?.sets?.get(i)!!
                    val setId = setData.metadata.set_id
                    val setTitle = setData.metadata.title
                    val progress = 0
                    val set = Set(
                        setId,
                        setTitle,
                        userid,
                        mutableListOf<Card>(),
                        progress,
                        SetCategory.COMMUNITY_SET
                    )

                    val cards : Response<GetCardsInSetResponse> = runBlocking {
                        return@runBlocking api.getCards(GetCardsInSetRequest(setId))
                    }
                    if (cards.isSuccessful) {
                        for (card in cards.body()!!.cards) {
                            set.cards.add(Card(card.card_id, card.phrase, false))
                        }
                    }
                    sets.add(set)
                }
            }
        } else {
            //page is the explore page ordered in terms of likes
            val response : Response<getPublicCardSetsOrderedByLikesResponse> = runBlocking {
                return@runBlocking api.getPublicCardSetsOrderedByLikes()
            }

            if (response.isSuccessful) {
                val size = response.body()!!.sets.size

                for(i in 0 until size) {
                    val setData = response.body()?.sets?.get(i)!!
                    val setId = setData.set_id
                    val setTitle = setData.title
                    val progress = 0
                    val set = Set(
                        setId,
                        setTitle,
                        userid,
                        mutableListOf<Card>(),
                        progress,
                        SetCategory.COMMUNITY_SET
                    )

                    val cards : Response<GetCardsInSetResponse> = runBlocking {
                        return@runBlocking api.getCards(GetCardsInSetRequest(setId))
                    }
                    if (cards.isSuccessful) {
                        for (card in cards.body()!!.cards) {
                            set.cards.add(Card(card.card_id, card.phrase, false))
                        }
                    }
                    sets.add(set)
                }
            }
        }
        println(page)
        println(sets)
        setAdapter = CommunitySetAdapter(sets, userid, page) { position -> onSetClick(position) }
        binding.rvSets.adapter = setAdapter
        binding.rvSets.layoutManager = LinearLayoutManager(context)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            DisplaySetsFragment(0).apply {
                arguments = Bundle().apply {
                }
            }
    }
}