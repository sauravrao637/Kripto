package com.camo.kripto.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.camo.kripto.R
import com.camo.kripto.data.model.Coin

class CoinAdapter(private val coins: ArrayList<Coin.CoinItem>):
    RecyclerView.Adapter<CoinAdapter.DataViewHolder>() {

    class DataViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        fun bind(coinItem: Coin.CoinItem) {
            itemView.apply {
                tvName.text = coinItem.name
//                Glide.with(imageViewAvatar.context)
//                    .load(user.avatar)
//                    .into(imageViewAvatar)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder =
        DataViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.coin_item, parent, false))

    override fun getItemCount(): Int = coins.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(coins[position])
    }

    fun addCoins(coins: List<Coin.CoinItem>) {
        this.coins.apply {
            clear()
            addAll(coins)
        }

    }

}
