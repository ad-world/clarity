package com.example.clarity.profile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import com.example.clarity.MainActivity
import com.example.clarity.R
import com.example.clarity.SessionManager
import com.example.clarity.databinding.FragmentProfileBinding
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.FollowerListResponse
import com.example.clarity.sdk.GetSetsByUsernameResponse
import com.example.clarity.sdk.GetUserResponse
import com.example.clarity.sdk.GetUserSetProgressRequest
import com.example.clarity.sdk.GetUserSetProgressResponse
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


class ProfileFragment : Fragment() {

    private val api = ClaritySDK().apiService
    private var _binding: FragmentProfileBinding? = null

    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

    private val binding get() = _binding!!
    //private lateinit var lineChart: LineChart
    private lateinit var pieChart: PieChart


    private var username: String = ""
    private var userId: Int = 0

    private var totalSets = 0
    private var totalCards = 0
    private var completedCards = 0
    private var completedSets = 0
    private var incompleteCards = 0
    private var incompleteSets = 0
    private var cardDates = mutableListOf<LocalDateTime>()
    private var setDates = mutableListOf<LocalDateTime>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //navigation
        lifecycleScope.launch {
            username = sessionManager.getUserName()
            userId = sessionManager.getUserId()
        }
        val user = getUser()?.user
        val followingList = getFollowing()?.followers
        val followersList = getFollowers()?.followers

        val dropdownView: ImageView = binding.dropdown

        dropdownView.setOnClickListener {
            val menu = PopupMenu(view.context, dropdownView)
            menu.inflate(R.menu.profile_menu) // Replace with your menu resource file
            menu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_settings -> {
                        true
                    }
                    R.id.logout -> {
                        logout()
                        true
                    }
                    // Add more menu item cases as needed
                    else -> false
                }
            }
            menu.show()
        }

        binding.firstLast.text = user?.firstname + " " + user?.lastname

        //FOLLOWERS
        val numFollowers = followersList?.size

        binding.followers.text = numFollowers.toString() + " Followers"
        val followers = binding.followers


        followers.setOnHoverListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_HOVER_ENTER -> {
                    followers.setTextColor(Color.GRAY)
                }
                MotionEvent.ACTION_HOVER_EXIT -> {
                    followers.setTextColor(Color.BLACK)
                }
            }
            false
        }

        binding.followers.setOnClickListener {
            //findNavController().navigate(R.id.)
        }

        //FOLLOWING

        val numFollowing = followingList?.size

        binding.following.text = numFollowing.toString() + " Following"
        val following = binding.following

        following.setOnHoverListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_HOVER_ENTER -> {
                    following.setTextColor(Color.GRAY)
                }
                MotionEvent.ACTION_HOVER_EXIT -> {
                    following.setTextColor(Color.BLACK)
                }
            }
            false
        }
        binding.following.setOnClickListener {
            //findNavController().navigate(R.id.)
        }


        //STREAKS
        val streak = user?.login_streak
        binding.streak.text = "\uD83D\uDD25" + streak.toString() + " Day Streak"

        super.onViewCreated(view, savedInstanceState)

        var selectedTab = 0
        updateInfo()
        println(cardDates)
        println(setDates)
        sets()



        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectedTab = tab.position
                if(selectedTab == 0) {
                    binding.lineChart.clear()
                    binding.pieChart.clear()
                    updateInfo()
                    sets()
                } else {
                    binding.lineChart.clear()
                    binding.pieChart.clear()
                    updateInfo()
                    cards()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

    }

    private fun logout() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }
    private fun cards() {
        binding.progress.text = "Cards Progress"
        binding.completedText.text = "Completed Cards"
        binding.completedNum.text = completedCards.toString()
        //get cards



//        val attempts = listOf(
//            Entry(0f, 10f),
//            Entry(1f, 20f),
//            Entry(2f, 15f),
//            Entry(3f, 30f),
//        )

        val earliest = cardDates.minOrNull()
        val range = getRanges(earliest)

        val attempts = ArrayList<Entry>()
        for (i in range.indices) {
            val range = range[i]
            val entry = range.second?.let { Entry(i.toFloat(), it.toFloat()) }
            if (entry != null) {
                attempts.add(entry)
            }
        }

        val lineDataSet = LineDataSet(attempts, "Data Set")
        val lineData = LineData(lineDataSet)
        val chart = binding.lineChart
        chart.data = lineData
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.axisRight.isEnabled = false
        chart.axisLeft.setDrawGridLines(false)
        chart.xAxis.setDrawGridLines(false)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.animateX(1000)
        chart.invalidate()

        pieChart = binding.pieChart
        val entries = mutableListOf<PieEntry>()
        entries.add(PieEntry(completedCards.toFloat() / totalCards, "Completed"))
        entries.add(PieEntry((incompleteCards.toFloat() / totalCards), "Incomplete"))
        //entries.add(PieEntry((notStartedCards.toFloat() / totalSavedCards), "Not Started"))
        val dataSet = PieDataSet(entries, "")
        val colourList = listOf(
            Color.parseColor("#C6C6C6"), //light grey
            Color.parseColor("#74ABFF"), //light blue
            //Color.parseColor("#3546D9") //blue
        )
        dataSet.colors = colourList
        val data = PieData(dataSet)
        pieChart.setUsePercentValues(true)
        pieChart.setDrawEntryLabels(false)
        pieChart.description.isEnabled = false
        pieChart.data = data
        pieChart.invalidate()


        //number of sets completed
        binding.completedNum.text = completedCards.toString()
    }
    private fun getRanges(earliest: LocalDateTime?): List<Pair<String, Long?>> {
        val now = LocalDateTime.now()
        val years = earliest?.until(now, ChronoUnit.YEARS)
        val months = earliest?.until(now, ChronoUnit.MONTHS)?.rem(12)
        val days = earliest?.until(now, ChronoUnit.DAYS)?.rem(30)

        return listOf(
            Pair("Years", years),
            Pair("Months", months),
            Pair("Days", days)
        )
    }
    private fun sets(){
        binding.completedText.text = "Completed Sets"
        binding.progress.text = "Saved Sets Progress"

        binding.completedNum.text = completedSets.toString()

        val earliest = setDates.minOrNull()
        val range = getRanges(earliest)

        val attempts = ArrayList<Entry>()
        for (i in range.indices) {
            val range = range[i]
            val entry = range.second?.let { Entry(i.toFloat(), it.toFloat()) }
            if (entry != null) {
                attempts.add(entry)
            }
        }
//        val attempts = listOf(
//            Entry(0f, 10f),
//            Entry(1f, 20f),
//            Entry(2f, 15f),
//            Entry(3f, 30f),
//        )

        val lineDataSet = LineDataSet(attempts, "Data Set")
        val lineData = LineData(lineDataSet)
        val chart = binding.lineChart
        chart.data = lineData
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.axisRight.isEnabled = false
        chart.axisLeft.setDrawGridLines(false)
        chart.xAxis.setDrawGridLines(false)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.animateX(1000)
        chart.invalidate()

        pieChart = binding.pieChart
        val entries = mutableListOf<PieEntry>()
        entries.add(PieEntry(completedSets.toFloat() / totalSets, "Completed"))
        entries.add(PieEntry((incompleteSets.toFloat() / totalSets), "Incomplete"))
        //entries.add(PieEntry((notStartedSets.toFloat() / totalSavedSets), "Not Started"))
        val dataSet = PieDataSet(entries, "")
        val colourList = listOf(
            Color.parseColor("#C6C6C6"), //light grey
            Color.parseColor("#74ABFF"), //light blue
            //Color.parseColor("#3546D9") //blue
        )
        dataSet.colors = colourList
        val data = PieData(dataSet)
        pieChart.setUsePercentValues(true)
        pieChart.setDrawEntryLabels(false)
        pieChart.description.isEnabled = false
        pieChart.data = data
        pieChart.invalidate()


        //number of sets completed
        binding.completedNum.text = completedSets.toString()
        //get sets
    }
    private fun getFollowers(): FollowerListResponse? {
        val response : Response<FollowerListResponse> = runBlocking {
            return@runBlocking api.getFollowers(userId)
        }
        return response.body()
    }
    private fun getFollowing(): FollowerListResponse? {
        val response : Response<FollowerListResponse> = runBlocking {
            return@runBlocking api.getFollowing(userId)
        }
        return response.body()
    }
    private fun getUser(): GetUserResponse? {
        val response : Response<GetUserResponse> = runBlocking {
            return@runBlocking api.getUser(username)
        }
        return response.body()
    }
    private fun updateInfo() {
        println("gdifugdnj")
        val response : Response<GetSetsByUsernameResponse> = runBlocking {
            return@runBlocking api.getSetsByUsername(username)
        }
        val sets = response.body()?.data
        totalSets = sets?.size!!
        totalCards = 0
        completedCards = 0
        completedSets = 0
        incompleteCards = 0
        incompleteSets = 0


        if (sets != null) {
            for (s in sets){
                val setReq = GetUserSetProgressRequest(s.set_id, userId)
                val res : Response<GetUserSetProgressResponse> = runBlocking {
                    return@runBlocking api.getSetProgress(setReq)
                }
                val currentSet = res.body()
                totalCards += currentSet?.numCards!!
                completedCards += currentSet?.numCompletedCards!!

                val completedList = currentSet?.completedCard

                //can check all cards that are completed and enter in their dates
//                val allCardsReq = GetCardsInSetRequest(s.set_id)
//                val allCardsRes : Response<GetCardsInSetResponse> = runBlocking {
//                    return@runBlocking api.getCards(allCardsReq)
//                }
//                val allCards = allCardsRes.body()?.cards
                var latestDate = LocalDateTime.of(-999_999_999, 1, 1, 0, 0, 0)
                if (completedList != null) {
                    for (c in completedList) {
                        //get card id and then check the completed date
                        val date = LocalDateTime.parse(c.completion_date)
                        cardDates.add(date)
                        if(date > latestDate) {
                            latestDate = date
                        }
                    }
                }
                if(currentSet?.numCards!! == currentSet?.numCompletedCards!!) {
                    completedSets += 1
                    setDates.add(latestDate)
                    //now check the last completion date which will give the date that the set was completed
                }
            }
        }
        incompleteCards = totalCards - completedCards
        incompleteSets = totalSets?.minus(completedSets)!!
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}