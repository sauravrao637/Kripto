package com.camo.kripto.ui.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.camo.kripto.data.model.CoinMarket
import com.camo.kripto.databinding.MarketCapItemBinding
import com.camo.kripto.ui.CoinActivity

class MarketCapAdapter(
    var curr: String,
    diffCallback: DiffUtil.ItemCallback<CoinMarket.CoinMarketItem>
) :
    PagingDataAdapter<CoinMarket.CoinMarketItem, MarketCapAdapter.DataHolder>(diffCallback) {

    private val TAG = MarketCapAdapter::class.simpleName
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
        this.context = parent.context
        return DataHolder(MarketCapItemBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: DataHolder, position: Int) {
        val coinMarketItem = getItem(position)
        if (coinMarketItem != null) {
            Glide.with(holder.root.context)
                .load(coinMarketItem.image)
                .into(holder.coinIv)
            holder.coinName.text = coinMarketItem.name
            holder.currPrice.text = String.format("%,8.2f%s", coinMarketItem.current_price, curr)
            holder.marketCap.text = String.format("%,8.0f%s", coinMarketItem.market_cap, curr)
            val perChange = coinMarketItem.market_cap_change
            if(perChange>=0) holder.priceChangePercentage.setTextColor(Color.GREEN)
            else{
                holder.priceChangePercentage.setTextColor(Color.RED)
            }
            var s = String.format("%.2f%%", perChange)
            holder.priceChangePercentage.text = s
            holder.root.setOnClickListener {
                launchActivity(coinMarketItem.id,curr)
            }
        } else {
            Log.d(TAG, "coinItem is null")
        }

    }

    private fun launchActivity(id: String?, curr: String) {
        val intent = Intent(context, CoinActivity::class.java)
        intent.putExtra("coinId", id)
        intent.putExtra("curr",curr)
        context.startActivity(intent)
    }


    class DataHolder(marketCapItemBinding: MarketCapItemBinding) :
        RecyclerView.ViewHolder(marketCapItemBinding.root) {
        val root = marketCapItemBinding.root
        val coinIv = marketCapItemBinding.ivCoin
        val coinName = marketCapItemBinding.tvCoinName
        val currPrice = marketCapItemBinding.tvCurrentPrice
        val marketCap = marketCapItemBinding.tvMarketCap
        val priceChangePercentage = marketCapItemBinding.tvDur
    }


    object Comparator : DiffUtil.ItemCallback<CoinMarket.CoinMarketItem>() {
        override fun areItemsTheSame(
            oldItem: CoinMarket.CoinMarketItem,
            newItem: CoinMarket.CoinMarketItem
        ): Boolean {
            // Id is unique.
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: CoinMarket.CoinMarketItem,
            newItem: CoinMarket.CoinMarketItem
        ): Boolean {
            return oldItem == newItem
        }
    }



}
