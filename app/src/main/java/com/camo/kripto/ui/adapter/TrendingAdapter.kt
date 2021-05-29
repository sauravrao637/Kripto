package com.camo.kripto.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.camo.kripto.R
import com.camo.kripto.remote.model.Trending
import com.camo.kripto.ui.presentation.coin.CoinActivity

class TrendingAdapter :
    RecyclerView.Adapter<TrendingAdapter.ViewHolder>() {

    var list: List<Trending.Coin>? = null
    var curr: String? = null
    lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tv_trending_coin)
        val imageView: ImageView = view.findViewById(R.id.iv_trending)
        val root = view.rootView
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        this.context = viewGroup.context
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.trending_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val coin = list?.get(position)?.item
        //TODO diable view binding for url_item
        if (coin != null) {
            holder.textView.text = coin.name
            Glide.with(holder.imageView.context)
                .load(coin.large)
                .into(holder.imageView)
            holder.root.setOnClickListener {
                launchActivity(coin.id, this.curr ?: "inr")
            }
        } else holder.textView.text = "NA"
    }

    private fun launchActivity(id: String?, curr: String) {
        val intent = Intent(context, CoinActivity::class.java)
        intent.putExtra("coinId", id)
        intent.putExtra("curr", curr)
        context.startActivity(intent)
    }

    override fun getItemCount() = list?.size ?: 0

}