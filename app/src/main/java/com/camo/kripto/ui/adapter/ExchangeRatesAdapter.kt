package com.camo.kripto.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigator
import androidx.recyclerview.widget.RecyclerView
import com.camo.kripto.databinding.ExchangeRatesItemBinding
import com.camo.kripto.remote.model.ExchangeRates
import com.camo.kripto.utils.Extras
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ExchangeRatesAdapter : RecyclerView.Adapter<ExchangeRatesAdapter.ViewHolder>() {
    var list: List<ExchangeRates.NVUT>? = null
    private var multiplier: Double = 1.0
    private var currencyFactor: Double = 1.0
    private var rates: Map<String, ExchangeRates.NVUT>? = null

    class ViewHolder(val binding: ExchangeRatesItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ExchangeRatesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list?.get(position)
        if (item != null) {
            holder.binding.tvCurrency.text = item.unit
            holder.binding.tvExchangeValue.text =
                Extras.getFormattedDouble((multiplier * item.value / currencyFactor))
        }
    }

    fun setMultiplierValue(d: Double) {
        multiplier = d
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    fun setData(data: Response<ExchangeRates>?) {
        this.rates = data?.body()?.rates
        val temp = data?.body()?.rates?.entries
        if (temp == null) {
            list = null
            return
        }
        val l = mutableListOf<ExchangeRates.NVUT>()
        for (i in temp) {
            l.add(i.value)
        }
        list = l
        notifyDataSetChanged()
    }

    fun currencyChanged(toString: String) {
        if (rates?.containsKey(toString) == true) {
            this.currencyFactor = rates!![toString]?.value ?: 1.0
        } else {
            this.currencyFactor = 1.0
        }
        notifyDataSetChanged()
    }
}