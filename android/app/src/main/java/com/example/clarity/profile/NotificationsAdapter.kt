package com.example.clarity.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.databinding.NotificationsCardBinding
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.MarkMessage
import kotlinx.coroutines.runBlocking
import com.example.clarity.sdk.Notification


private val api = ClaritySDK().apiService

class NotificationsAdapter(private var items: List<Notification>, private val userid: Int) :

    RecyclerView.Adapter<NotificationsAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: NotificationsCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var binding = NotificationsCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items.get(position)

        holder.binding.date.text = item.notificationDate
        holder.binding.message.text = item.message

        holder.binding.parent.setOnClickListener {
            if(holder.binding.dot.visibility == View.VISIBLE) {
                //now we need to mark this as read
                runBlocking {
                    return@runBlocking api.markMessage(MarkMessage(item.notificationId, 1))
                }
                holder.binding.dot.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = items.size

}