package com.example.clarity.sets

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import java.lang.Integer.min

class WordFilterAdapter(context: Context, private val suggestions: Array<String>) :
    BaseAdapter(), Filterable {

    private var filteredSuggestions: List<String> = suggestions.toList()
    private var oldText: String = ""

    override fun getCount(): Int {
        return filteredSuggestions.size
    }

    override fun getItem(position: Int): Any {
        return filteredSuggestions[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        Log.d("getView","called")
        if (convertView == null) {
            view = LayoutInflater.from(parent?.context).inflate(android.R.layout.simple_dropdown_item_1line, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        Log.d("titleholder: ", viewHolder.itemTextView.text as String)

        viewHolder.itemTextView.text = filteredSuggestions[position]

        return view
    }

    private class ViewHolder(view: View) {
        val itemTextView: TextView = view.findViewById(android.R.id.text1)
    }

    override fun getFilter(): Filter {
        return wordFilter
    }

    fun getCurrentWord(newText: String, oldText: String): String {
        val noCurrentWord = "abc"
        val newTextWords = newText.split(" ")
        val oldTextWords = oldText.split(" ")
        Log.d("newText", newText)
        Log.d("oldText", oldText)

        if (kotlin.math.abs(newText.length - oldText.length) > 1) {
            Log.d("returning Current Word: ", noCurrentWord)
            return noCurrentWord
        }

        var index = 0
        val min = min(newTextWords.size, oldTextWords.size)

        while(index < min) {
            if (newTextWords[index] != oldTextWords[index]) {
                Log.d("returning Current Word: ", newTextWords[index])
                return newTextWords[index]
            }
            index++
        }

        if (index < newTextWords.size) {
            Log.d("returning Current Word: ", newTextWords[index])
            return newTextWords[index]
        }

        Log.d("returning Current Word: ", noCurrentWord)
        return noCurrentWord
    }

    private val wordFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults? {
            if (oldText == constraint.toString()) {
                return null
            }

            val filterResults = FilterResults()

            if (constraint.isNullOrEmpty()) {
                filterResults.values = suggestions.toList()
            } else {
                val currentWord = getCurrentWord(constraint.toString(), oldText)
                filterResults.values = suggestions.filter { it.startsWith(currentWord, ignoreCase = true) }
            }

            oldText = constraint.toString()
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            @Suppress("UNCHECKED_CAST")
            if (results != null) {
                filteredSuggestions = results.values as? List<String> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}