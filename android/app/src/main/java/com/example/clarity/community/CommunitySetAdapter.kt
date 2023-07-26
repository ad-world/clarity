package com.example.clarity.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.R
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.ClonePublicSetRequest
import com.example.clarity.sdk.ClonePublicSetResponse
import com.example.clarity.sdk.FollowerListResponse
import com.example.clarity.sdk.FollowingRequestEntity
import com.example.clarity.sdk.GetSetsByUsernameResponse
import com.example.clarity.sdk.GetUserResponse
import com.example.clarity.sdk.LikeCardSetRequest
import com.example.clarity.sdk.SetMetadata
import com.example.clarity.sdk.UnlikeCardSetRequest
import com.example.clarity.sets.data.Set
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class CommunitySetAdapter(
    private val sets: MutableList<Set>,
    private val userid: Int,
    private val page: Int,
    private val onItemClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<CommunitySetAdapter.SetViewHolder>() {

    private val api = ClaritySDK().apiService
    class SetViewHolder(itemView: View, private val onItemClicked: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            onItemClicked(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {

        return SetViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.set_card_community,
                parent,
                false
            ), onItemClicked
        )
    }

    override fun getItemCount(): Int {
        return sets.size
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val set = sets[position]
        println("Position: $position")
        holder.itemView.apply {
            val tvSetTitle = findViewById<TextView>(R.id.tvSetTitle)
            var user = findViewById<TextView>(R.id.User)
            val tvCardCount = findViewById<TextView>(R.id.tvCardCount)


            tvSetTitle.text = set.title
            if (set.cards.size == 1) {
                tvCardCount.text = "${set.cards.size} Card"
            } else {
                tvCardCount.text = "${set.cards.size} Cards"
            }
            val userId = set?.userId

            val req = getUser(userId.toString())?.user
            user.text = req?.username

            val liked = findViewById<ImageView>(R.id.liked)
            val notLiked = findViewById<ImageView>(R.id.notLiked)
            val follow = findViewById<TextView>(R.id.Follow)

            val download = findViewById<ImageView>(R.id.clone)

            val savedSets = getSets()
            if(alreadySaved(savedSets, set.id)) {
                download.visibility = View.GONE
            } else {
                download.visibility = View.VISIBLE
            }

            download.setOnClickListener {
                val response : Response<ClonePublicSetResponse> = runBlocking {
                    return@runBlocking api.clonePublicSet(ClonePublicSetRequest(set.id, userid))
                }
                download.visibility = View.GONE
            }


            val following = getFollowing()
            if(doesFollow(following, userId)) {
                follow.text = "Unfollow"
            } else {
                follow.text = "Follow"
            }
            follow.setOnClickListener {
                if(follow.text == "Unfollow") {
                    //going to unfollow
                    if(userId != null) {
                        runBlocking {
                            return@runBlocking api.unfollow(FollowingRequestEntity(userid, userId))
                        }
                        if(page == 1) {
                            notifyItemRemoved(position)
                        }

                        follow.text = "Follow"
                    }

                } else {
                    //going to follow
                    if(userId != null) {
                        runBlocking {
                            return@runBlocking api.follow(FollowingRequestEntity(userid, userId))
                        }
                        follow.text = "Unfollow"
                    }
                }
            }

            liked.setOnClickListener {
                if(liked.visibility == View.VISIBLE) {
                    //already liked, now is going to be unliked
                    runBlocking {
                        return@runBlocking api.likeCardSet(LikeCardSetRequest(userid, set.id))
                    }
                    liked.visibility = View.GONE
                    notLiked.visibility = View.VISIBLE
                }
            }
            notLiked.setOnClickListener {
                if(notLiked.visibility == View.VISIBLE) {
                    //already liked, now is going to be unliked
                    runBlocking {
                        return@runBlocking api.unlikeCardSet(UnlikeCardSetRequest(userid, set.id))
                    }
                    liked.visibility = View.VISIBLE
                    notLiked.visibility = View.GONE
                }
            }
        }
    }
    private fun getUser(userId: String): GetUserResponse? {
        val response : Response<GetUserResponse> = runBlocking {
            return@runBlocking api.getUserById(userId)
        }
        return response.body()
    }
    private fun getFollowing(): List<Int>? {
        val response : Response<FollowerListResponse> = runBlocking {
            return@runBlocking api.getFollowing(userid)
        }
        return response.body()?.followers
    }
    private fun getSets(): List<SetMetadata>? {
        val response : Response<GetSetsByUsernameResponse> = runBlocking {
            return@runBlocking api.getSetsByUsername(userid.toString())
        }
        return response.body()?.data
    }
    private fun alreadySaved(list:  List<SetMetadata>?, setid: Int): Boolean {
        if (list != null) {
            for (item in list) {
                if(setid == item.set_id){
                    return true
                }
            }
        }
        return false
    }
    private fun doesFollow(list: List<Int>?, setUser: Int?): Boolean {
        if (list != null) {
            for (item in list) {
                if(setUser == item){
                    return true
                }
            }
        }
        return false
    }
}