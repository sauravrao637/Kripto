package com.camo.kripto.ui.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.camo.kripto.R
import com.camo.kripto.databinding.ExchangesItemBinding
import com.camo.kripto.remote.model.Exchanges
import com.camo.kripto.utils.Extras
import java.math.BigDecimal
import java.util.*


class ExchangesAdapter(diffCallback: DiffUtil.ItemCallback<Exchanges.ExchangesItem>) :
    PagingDataAdapter<Exchanges.ExchangesItem, ExchangesAdapter.DataHolder>(diffCallback) {
    private var mExpandedPosition = RecyclerView.NO_POSITION
    private var context: Context? = null

    class DataHolder(val binding: ExchangesItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: DataHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            val isExpanded = position == mExpandedPosition
            holder.binding.details.visibility = if (isExpanded) View.VISIBLE else View.GONE
            holder.itemView.isActivated = isExpanded
            holder.itemView.setOnClickListener {
                mExpandedPosition = if (isExpanded) RecyclerView.NO_POSITION else position
                notifyDataSetChanged()
            }
            holder.binding.tvExchangesTrust.text = item.trust_score.toString()
            holder.binding.tvCountry.text = String.format("Country of origin :- %s",item.country?:"NA")
            if(item.year_established!=0)holder.binding.tvYearEstablished.text = String.format("Established in %d",item.year_established)
            else{
                holder.binding.tvYearEstablished.visibility = View.GONE
            }
            holder.binding.tvExchageDesc.text = String.format("%s",
                item.name.toUpperCase(Locale.ROOT),item.description)
            if(item.description!=null && item.description.isEmpty())holder.binding.tvExchageDesc.visibility = View.GONE
            if (item.trust_score < 5) {
                val color = R.color.primaryColorRed
                for (drawable in holder.binding.tvExchangesTrust.compoundDrawablesRelative) {
                    if (drawable != null) {
                        drawable.colorFilter =
                            PorterDuffColorFilter(
                                ContextCompat.getColor(
                                    holder.binding.tvExchangesTrust.context,
                                    color
                                ),
                                PorterDuff.Mode.SRC_IN
                            )
                    }
                }
            }

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