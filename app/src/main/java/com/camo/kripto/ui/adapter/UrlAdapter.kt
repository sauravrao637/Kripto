package com.camo.kripto.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.camo.kripto.R

class UrlAdapter(private val dataSet: List<String?>?) :
    RecyclerView.Adapter<UrlAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tv_url)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.url_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        //TODO diable view binding for url_item
        viewHolder.textView.text = dataSet?.get(position)?:"NA"
    }

    override fun getItemCount() = dataSet?.size?:0
}