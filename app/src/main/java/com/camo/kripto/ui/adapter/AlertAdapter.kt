package com.camo.kripto.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.camo.kripto.databinding.AlertItemBinding
import com.camo.kripto.local.model.PriceAlert
import timber.log.Timber

class AlertAdapter(
    private val onAlertItemListener: OnAlertItemListener
) :
    RecyclerView.Adapter<AlertAdapter.ViewHolder>() {
    private var list: List<PriceAlert>? = null

    class ViewHolder(
        val binding: AlertItemBinding,
        private val onAlertItemListener: OnAlertItemListener
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.switchEnable.setOnCheckedChangeListener { buttonView, isChecked ->
                onAlertItemListener.onAlertToggle(absoluteAdapterPosition, isChecked)
            }
            binding.ibDelete.setOnClickListener {
                onAlertItemListener.deleteAlert(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AlertItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onAlertItemListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list?.get(position)
        if (item != null) {
            holder.binding.tvCoinName.text = item.name
            holder.binding.switchEnable.isChecked = item.enabled
            holder.binding.tvLessThan.text = if (item.lessThan == "0") "NA" else item.lessThan
            holder.binding.tvMoreThan.text =
                if (item.moreThan == Double.MAX_VALUE.toString()) "NA" else item.moreThan
        } else {
            Timber.d("%s %d", "PriceAlert item null at", position)
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    fun setData(it: List<PriceAlert>?) {
        list = it
        notifyDataSetChanged()
    }

    fun getItem(position: Int): PriceAlert? {
        return list?.get(position)
    }

    interface OnAlertItemListener {
        fun onAlertToggle(position: Int, boolean: Boolean)
        fun onAlertEdit(position: Int)
        fun deleteAlert(position: Int)
    }
}
