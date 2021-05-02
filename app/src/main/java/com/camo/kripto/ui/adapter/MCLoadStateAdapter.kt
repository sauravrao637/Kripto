package com.camo.kripto.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.camo.kripto.R
import com.camo.kripto.databinding.LoadMarketCapFooterViewItemBinding

class MCLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<MCLoadStateAdapter.MCLoadStateViewHolder>() {

    private val TAG = MCLoadStateAdapter::class.simpleName

    override fun onBindViewHolder(holder: MCLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): MCLoadStateViewHolder {
        return MCLoadStateViewHolder.create(parent, retry)
    }


    class MCLoadStateViewHolder(
        private val binding: LoadMarketCapFooterViewItemBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val TAG = MCLoadStateAdapter::class.simpleName

        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = "some error"
                Log.d(TAG,loadState.error.localizedMessage?:"error")
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error
        }

        companion object {
            fun create(parent: ViewGroup, retry: () -> Unit): MCLoadStateViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.load_market_cap_footer_view_item, parent, false)
                val binding = LoadMarketCapFooterViewItemBinding.bind(view)
                return MCLoadStateViewHolder(binding, retry)
            }
        }
    }
}