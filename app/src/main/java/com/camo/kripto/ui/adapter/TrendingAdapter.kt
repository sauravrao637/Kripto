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
import com.camo.kripto.ui.presentation.coin.CoinIdNameKeys.COIN_ID_KEY
import com.camo.kripto.ui.presentation.coin.CoinIdNameKeys.COIN_NAME_KEY

class TrendingAdapter :
    RecyclerView.Adapter<TrendingAdapter.ViewHolder>() {

    var list: List<Trending.Coin>? = null
    lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tv_trending_coin)
        val imageView: ImageView = view.findViewById(R.id.iv_trending)
        val root: View = view.rootView
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        this.context = viewGroup.context
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.trending_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val coin = list?.get(position)?.item
        //TODO disable view binding for url_item
        if (coin != null) {
            holder.textView.text = coin.name
            Glide.with(holder.imageView.context)
                .load(coin.large)
                .into(holder.imageView)
            holder.root.setOnClickListener {
                launchActivity(coin.id, coin.name)
            }
        } else holder.textView.text = context.getString(R.string.na)
    }

    private fun launchActivity(id: String?, name: String) {
        val intent = Intent(context, CoinActivity::class.java)
        intent.putExtra(COIN_ID_KEY, id)
        intent.putExtra(COIN_NAME_KEY, name)
        context.startActivity(intent)
    }

    override fun getItemCount() = list?.size ?: 0
    fun setData(coins: List<Trending.Coin>?) {
        list = coins
        notifyDataSetChanged()
    }

}