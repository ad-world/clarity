package com.example.clarity.profile

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.R
import com.example.clarity.SessionManager
import com.example.clarity.databinding.CardBinding
import com.example.clarity.databinding.FollowCardBinding
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.FollowerListResponse
import com.example.clarity.sdk.FollowingRequestEntity
import com.example.clarity.sdk.FollowingResponse
import com.example.clarity.sdk.GetUserResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response


private val api = ClaritySDK().apiService

class FollowAdapter(private var items: List<Int>, val followers: Boolean, val userid: Int) :

    RecyclerView.Adapter<FollowAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: FollowCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var binding = FollowCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        val user = getUser(item)?.user
        println("B")
        println(user)
        holder.binding.name.text = user?.firstname + " " + user?.lastname
        if(!followers) {
            holder.binding.remove.text = "Unfollow"
        }
        holder.binding.remove.setOnClickListener() {
            if(followers) {
                //remover follower
                val request = user?.user_id?.let { it1 -> FollowingRequestEntity(it1, userid) }
                runBlocking {
                    return@runBlocking request?.let { it1 -> api.unfollow(it1) }!!
                }
                val updatedList = items.toMutableList()
                updatedList.removeAt(position)
                items = updatedList.toList()
                notifyItemRemoved(position)
            } else {
                //stop following
                val request = user?.user_id?.let { it1 -> FollowingRequestEntity(userid, it1) }
                runBlocking {
                    return@runBlocking request?.let { it1 -> api.unfollow(it1) }!!
                }
                val updatedList = items.toMutableList()
                updatedList.removeAt(position)
                items = updatedList.toList()
                notifyItemRemoved(position)
            }
        }
    }

    override fun getItemCount(): Int = items.size
    private fun getUser(userid: Int): GetUserResponse? {
        val response : Response<GetUserResponse> = runBlocking {
            return@runBlocking api.getUserById(userid.toString())
        }
        return response.body()
    }

}