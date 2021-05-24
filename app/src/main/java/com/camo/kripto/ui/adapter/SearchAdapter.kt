package com.camo.kripto.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.camo.kripto.databinding.CoinItemBinding
import com.camo.kripto.local.model.Coin
import com.camo.kripto.ui.presentation.coin.CoinActivity
import com.camo.kripto.ui.presentation.search.SearchActivity

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ViewHolder>(){

    var list: List<Coin>? = null
    var curr: String? = null

    lateinit var context: Context

    class ViewHolder(val binding: CoinItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        this.context = viewGroup.context
        val binding = CoinItemBinding.inflate(LayoutInflater.from(this.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position<itemCount) {
            val coin = list?.get(position)
            if (coin != null) {
                holder.binding.tvCoinSymbol.text = coin.symbol
                holder.binding.tvCoinName.text = coin.name
                holder.binding.root.setOnClickListener {
                    launchActivity(coin.id, this.curr ?: "inr")
                }
            }
        }
    }

    private fun launchActivity(id: String?, curr: String) {
        val intent = Intent(context, CoinActivity::class.java)
        intent.putExtra("coinId", id)
        intent.putExtra("curr", curr)
        context.startActivity(intent)
        (context as SearchActivity).finish()
    }

    override fun getItemCount() = list?.size ?: 0
    fun setData(coins: List<Coin>) {
        this.list = coins
        notifyDataSetChanged()
    }


}
