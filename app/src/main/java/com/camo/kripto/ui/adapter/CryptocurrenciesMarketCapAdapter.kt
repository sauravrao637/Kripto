package com.camo.kripto.ui.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.camo.kripto.remote.model.CoinMarket
import com.camo.kripto.databinding.CryptocurrenciesMarketCapItemBinding
import com.camo.kripto.ui.presentation.coin.CoinActivity
import com.camo.kripto.utils.Extras
import timber.log.Timber

class CryptocurrenciesMarketCapAdapter(
    var curr: String,
    diffCallback: DiffUtil.ItemCallback<CoinMarket.CoinMarketItem>
) :
    PagingDataAdapter<CoinMarket.CoinMarketItem, CryptocurrenciesMarketCapAdapter.DataHolder>(diffCallback) {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
        this.context = parent.context
        return DataHolder(CryptocurrenciesMarketCapItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: DataHolder, position: Int) {
        val coinMarketItem = getItem(position)

        if (coinMarketItem != null) {

            Glide.with(holder.binding.root.context)
                .load(coinMarketItem.image)
                .fitCenter()
                .into(holder.binding.ivCoin)

            holder.binding.tvCoinName.text = coinMarketItem.symbol
            holder.binding.tvCurrentPrice.text = Extras.getFormattedDoubleCurr(
                coinMarketItem.current_price,
                curr,
                suffix = ""
            )
            holder.binding.tvMarketCap.text = Extras.getFormattedDoubleCurr(
                coinMarketItem.market_cap,
                curr,
                suffix = ""
            )

            val perChange = coinMarketItem.market_cap_change
            if(perChange>=0) holder.binding.tvCryprocurrenciesMarketCapItemPerChange.setTextColor(Color.GREEN)
            else{
                holder.binding.tvCryprocurrenciesMarketCapItemPerChange.setTextColor(Color.RED)
            }
            holder.binding.tvCryprocurrenciesMarketCapItemPerChange.text = Extras.getFormattedPerChange(perChange)

            holder.binding.root.setOnClickListener {
                launchActivity(coinMarketItem.id,coinMarketItem.name)
            }
        } else {
            Timber.d( "coinItem is null")
        }

    }

    private fun launchActivity(id: String?, name: String) {
        val intent = Intent(context, CoinActivity::class.java)
        intent.putExtra(CoinActivity.COIN_ID_KEY, id)
        intent.putExtra(CoinActivity.COIN_NAME_KEY,name)
        context.startActivity(intent)
    }

    fun getCoin(bindingAdapterPosition: Int): CoinMarket.CoinMarketItem? {
        return getItem(bindingAdapterPosition)
    }

    class DataHolder(val binding: CryptocurrenciesMarketCapItemBinding) :
        RecyclerView.ViewHolder(binding.root)

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
