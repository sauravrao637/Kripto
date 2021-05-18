package com.camo.kripto.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.camo.kripto.databinding.ExchangesItemBinding
import com.camo.kripto.remote.model.Exchanges
import com.camo.kripto.utils.Extras


class ExchangesAdapter(diffCallback: DiffUtil.ItemCallback<Exchanges.ExchangesItem>) :
    PagingDataAdapter<Exchanges.ExchangesItem, ExchangesAdapter.DataHolder>(diffCallback) {

    private var context: Context? = null

    class DataHolder(exchangesItemBinding: ExchangesItemBinding) :
        RecyclerView.ViewHolder(exchangesItemBinding.root) {
            val binding = exchangesItemBinding
        }

    override fun onBindViewHolder(holder: DataHolder, position: Int) {
        val item = getItem(position)
        if(item!=null) {
            holder.binding.tvExchangesTrust.text = item.trust_score.toString()
            Glide.with(holder.binding.root.context)
                .load(item.image)
                .fitCenter()
                .into(holder.binding.ivExchanges1)
            holder.binding.tvExchangesName.text = item.name
            holder.binding.tvExchangesTradeVol.text = Extras.getFormattedDoubleCurr(
                item.trade_volume_24h_btc,
                "BTC",
                suffix = " 24h Trading Vol"
            )
            holder.binding.ivExchangesLink.setOnClickListener { Extras.browse(item.url, context) }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
        this.context = parent.context
        return DataHolder(
            ExchangesItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    object Comparator : DiffUtil.ItemCallback<Exchanges.ExchangesItem>() {
        override fun areItemsTheSame(
            oldItem: Exchanges.ExchangesItem,
            newItem: Exchanges.ExchangesItem
        ): Boolean {
            // Id is unique.
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Exchanges.ExchangesItem,
            newItem: Exchanges.ExchangesItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}