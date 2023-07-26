package com.example.clarity.classroompage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clarity.R
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.GetCardsInSetRequest
import com.example.clarity.sdk.GetCardsInSetResponse
import com.example.clarity.sdk.StatusResponse
import com.example.clarity.sdk.Task
import com.example.clarity.sdk.TaskWithProgress
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class ClassroomTeacherTaskAdapter(
    private val tasks: MutableList<TaskWithProgress>,
    private val onItemClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<ClassroomTeacherTaskAdapter.SetViewHolder>() {

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
                R.layout.task_card,
                parent,
                false
            ), onItemClicked
        )
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val task = tasks[position]
        println("Position: $position")
        holder.itemView.apply {
            val tvSetTitle = findViewById<TextView>(R.id.tvSetTitle)
            val tvCardCount = findViewById<TextView>(R.id.tvCardCount)
            val tvDescription = findViewById<TextView>(R.id.tvDescription)

            tvSetTitle.text = task.name
            tvDescription.text = task.description

            var numberOfCards = 0
            // find the number of cards in a set
            val getCardsInSetRequest = GetCardsInSetRequest(task.setId)
            // make the api call to get list of cards
            val response: Response<GetCardsInSetResponse> = runBlocking {
                return@runBlocking api.getCards(getCardsInSetRequest)
            }
            // check if response was valid
            if (response.body()?.response == StatusResponse.Success) {
                numberOfCards = response.body()?.cards?.size ?: 0
            }
            // set the text on the task card accordingly to the number of cards there are
            if (numberOfCards == 1) {
                tvCardCount.text = "$numberOfCards Card"
            } else {
                tvCardCount.text = "$numberOfCards Cards"
            }
        }
    }
}