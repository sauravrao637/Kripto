package com.camo.kripto.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.camo.kripto.R
import com.camo.kripto.databinding.LoadFooterViewItemBinding
import timber.log.Timber
//CoinMarketCapLoadStateAdapter
class CMCLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<CMCLoadStateAdapter.MCLoadStateViewHolder>() {

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
        private val binding: LoadFooterViewItemBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                //TODO better error displaying
                binding.errorMsg.text = binding.root.context.getString(R.string.error)
                Timber.d(loadState.error.localizedMessage?:"error")
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error
        }

        companion object {
            fun create(parent: ViewGroup, retry: () -> Unit): MCLoadStateViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.load_footer_view_item, parent, false)
                val binding = LoadFooterViewItemBinding.bind(view)
                return MCLoadStateViewHolder(binding, retry)
            }
        }
    }
}