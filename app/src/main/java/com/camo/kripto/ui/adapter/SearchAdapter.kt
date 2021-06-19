package com.camo.kripto.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.camo.kripto.databinding.CoinItemBinding
import com.camo.kripto.local.model.Coin
import com.camo.kripto.repos.Repository
import com.camo.kripto.ui.presentation.PriceAlertActivity
import com.camo.kripto.ui.presentation.coin.CoinActivity
import com.camo.kripto.ui.presentation.coin.CoinIdNameKeys.COIN_ID_KEY
import com.camo.kripto.ui.presentation.coin.CoinIdNameKeys.COIN_NAME_KEY
import com.camo.kripto.ui.presentation.search.SearchActivity
import javax.inject.Inject

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    var list: List<Coin>? = null

    /**
     * Custom on long click item listener.
     */
    var mOnLongItemClickListener: OnLongItemClickListener? = null

    fun setOnLongItemClickListener(OnLongItemClickListener: OnLongItemClickListener?) {
        mOnLongItemClickListener = OnLongItemClickListener
    }

    interface OnLongItemClickListener {
        fun itemLongClicked(v: View?, position: Int)
    }
    @Inject
    lateinit var repository: Repository
    lateinit var context: Context

    class ViewHolder(val binding: CoinItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        this.context = viewGroup.context
        val binding = CoinItemBinding.inflate(LayoutInflater.from(this.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list == null) return
        val coin = list!![position]
        holder.binding.tvCoinSymbol.text = coin.symbol
        holder.binding.tvCoinName.text = coin.name
        holder.binding.root.setOnClickListener {
            launchActivity(coin.id, coin.name)
        }
        holder.itemView.setOnLongClickListener { v ->
            if (mOnLongItemClickListener != null) {
                mOnLongItemClickListener!!.itemLongClicked(v, position)
            }
            true
        }
    }

    private fun launchActivity(id: String?, name: String) {
        val intent = Intent(context, CoinActivity::class.java)
        intent.putExtra(COIN_ID_KEY, id)
        intent.putExtra(COIN_NAME_KEY, name)
        context.startActivity(intent)
        (context as SearchActivity).finish()
    }

    override fun getItemCount() = list?.size ?: 0
    fun setData(coins: List<Coin>?) {
        this.list = coins
        notifyDataSetChanged()
    }

    fun getItem(mCurrentItemPosition: Int): Coin? {
        if(list==null) return null
        return list!![mCurrentItemPosition]
    }
}
