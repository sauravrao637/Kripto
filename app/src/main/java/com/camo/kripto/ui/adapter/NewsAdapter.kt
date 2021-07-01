package com.camo.kripto.ui.adapter

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.camo.kripto.databinding.NewsItemBinding
import com.camo.kripto.remote.model.News
import com.camo.kripto.ui.Animations
import com.camo.kripto.ui.presentation.coin.CoinActivity
import com.camo.kripto.ui.presentation.coin.CoinIdNameKeys.COIN_ID_KEY
import com.camo.kripto.ui.presentation.coin.CoinIdNameKeys.COIN_NAME_KEY
import timber.log.Timber

class NewsAdapter(
    val type: String,
    diffCallback: DiffUtil.ItemCallback<News.StatusUpdate>
) :
    PagingDataAdapter<News.StatusUpdate, NewsAdapter.DataHolder>(
        diffCallback
    ) {

    lateinit var context: Context
    private var expandedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
        this.context = parent.context
        return DataHolder(
            NewsItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DataHolder, position: Int) {
        val newsItem = getItem(position)
        if (newsItem != null) {
            //TODO save news offline
            Glide.with(holder.binding.root.context)
                .load(newsItem.project.image.large)
                .fitCenter()
                .into(holder.binding.ivNewsItem)
            holder.binding.tvDesc.text = newsItem.description
            holder.binding.tvUser.text = String.format("- %s", newsItem.user ?: "NA")
            holder.binding.tvProjectName.text = newsItem.project.name
            holder.binding.ivNewsItem.setOnClickListener {
                launchCoinActivity(newsItem.project.id, newsItem.project.name)
            }
            if (position != expandedPosition) {
                holder.binding.tvDesc.apply {
                    maxLines = 2
                    ellipsize = TextUtils.TruncateAt.END
                }
                holder.binding.viewMoreBtn.animate().rotation(0F)
            }
            holder.binding.viewMoreBtn.setOnClickListener { v ->
                if (expandedPosition != -1) notifyItemChanged(expandedPosition)
                val show = Animations.toggleArrow(v, expandedPosition != position)
                if (!show) {
                    holder.binding.tvDesc.apply {
                        maxLines = 2
                        ellipsize = TextUtils.TruncateAt.END
                    }
                    expandedPosition = -1
                } else {
                    holder.binding.tvDesc.apply {
                        maxLines = 100
                        ellipsize = null
                    }
                    expandedPosition = position
                }
            }

        } else {
            Timber.d("newsItem is null")
        }
    }

    private fun launchCoinActivity(id: String, name: String) {
        val intent = Intent(context, CoinActivity::class.java)
        intent.putExtra(COIN_ID_KEY, id)
        intent.putExtra(COIN_NAME_KEY, name)
        context.startActivity(intent)
    }

    fun getNews(bindingAdapterPosition: Int): News.StatusUpdate? {
        return getItem(bindingAdapterPosition)
    }

    class DataHolder(val binding: NewsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    object Comparator : DiffUtil.ItemCallback<News.StatusUpdate>() {
        override fun areItemsTheSame(
            oldItem: News.StatusUpdate,
            newItem: News.StatusUpdate
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: News.StatusUpdate,
            newItem: News.StatusUpdate
        ): Boolean {
            return oldItem.created_at == newItem.created_at
        }
    }
}