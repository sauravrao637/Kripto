package com.camo.kripto.ui.presentation.about

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.camo.kripto.databinding.ContributorBinding
import com.camo.kripto.utils.Extras

class ContributorAdapter : RecyclerView.Adapter<ContributorAdapter.ContributorVH>() {

    private lateinit var context: Context
    private var contributors: Contributors? = null

    class ContributorVH(val binding: ContributorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContributorVH {
        this.context = parent.context
        return ContributorVH(
            ContributorBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ContributorVH, position: Int) {
        val item = contributors?.get(position)
        if (item != null) {
            Glide.with(holder.binding.root.context)
                .load(item.avatar_url)
                .fitCenter()
                .into(holder.binding.ivContributorAvatar)

            holder.binding.tvContributorName.text = item.login

            holder.binding.tvCommits.text = item.contributions.toString()
            holder.binding.root.setOnClickListener {
                Extras.browse(item.html_url, context)
            }
        }
    }

    override fun getItemCount(): Int {
        return contributors?.size ?: 0
    }

    fun setData(contributors: Contributors?) {
        this.contributors = contributors
        notifyDataSetChanged()
    }
}