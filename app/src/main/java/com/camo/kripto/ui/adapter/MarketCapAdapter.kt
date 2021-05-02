package com.camo.kripto.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.camo.kripto.data.model.CoinMarket
import com.camo.kripto.databinding.MarketCapItemBinding
import java.util.*
import kotlin.collections.ArrayList

class MarketCapAdapter(var curr: String,
                       diffCallback: DiffUtil.ItemCallback<CoinMarket.CoinMarketItem>
) :
    PagingDataAdapter<CoinMarket.CoinMarketItem,MarketCapAdapter.DataHolder>(diffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
        return DataHolder(MarketCapItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: DataHolder, position: Int) {
        val coinMarketItem = getItem(position)
        if (coinMarketItem != null) {
            Glide.with(holder.root.context)
                .load(coinMarketItem.image)
                .into(holder.coinIv)
        };
        holder.coinName.text = coinMarketItem?.name
        holder.currPrice.text = String.format(Locale.getDefault(), coinMarketItem?.current_price.toString()+" "+curr)

    }


    class DataHolder(marketCapItemBinding: MarketCapItemBinding) :
        RecyclerView.ViewHolder(marketCapItemBinding.root) {
        val root = marketCapItemBinding.root
        val coinIv = marketCapItemBinding.ivCoin
        val coinName = marketCapItemBinding.tvCoinName
        val currPrice = marketCapItemBinding.tvCurrentPrice
    }



    object Comparator : DiffUtil.ItemCallback<CoinMarket.CoinMarketItem>() {
        override fun areItemsTheSame(oldItem: CoinMarket.CoinMarketItem, newItem: CoinMarket.CoinMarketItem): Boolean {
            // Id is unique.
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CoinMarket.CoinMarketItem, newItem: CoinMarket.CoinMarketItem): Boolean {
            return oldItem == newItem
        }
    }

}
