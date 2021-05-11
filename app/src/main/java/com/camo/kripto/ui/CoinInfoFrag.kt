package com.camo.kripto.ui

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.api.RetrofitBuilder
import com.camo.kripto.data.model.CoinCD
import com.camo.kripto.databinding.FragCoinInfoBinding
import com.camo.kripto.ui.adapter.UrlAdapter
import com.camo.kripto.ui.base.VMFactory
import com.camo.kripto.ui.viewModel.CoinActivityVM
import timber.log.Timber


class CoinInfoFrag : Fragment() {


    private lateinit var binding: FragCoinInfoBinding
    private lateinit var viewModel: CoinActivityVM
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragCoinInfoBinding.inflate(inflater, container, false)
        binding.root.visibility = View.INVISIBLE
        setupViewModel()
        setupUI()
        setupObservers()

        return binding.root
    }

    private fun setupObservers() {
        viewModel.currentCoinData.observe(viewLifecycleOwner, {
            if (it != null) {
                coinChanged(it)
                binding.root.visibility = View.VISIBLE
            } else {

                Timber.d( "coinData null")
            }
        })
    }


    private fun coinChanged(coinCD: CoinCD) {

        binding.rvHomepageUrls.adapter = UrlAdapter(coinCD.links.homepage)
        binding.rvHomepageUrls.setHasFixedSize(true)
        binding.rvOfficialForumUrls.adapter = UrlAdapter(coinCD.links.blockchain_site)
        binding.rvOfficialForumUrls.setHasFixedSize(true)
        binding.rvBlockchainUrls.adapter = UrlAdapter(coinCD.links.official_forum_url)
        binding.rvBlockchainUrls.setHasFixedSize(true)
        val others = ArrayList<String?>()
        coinCD.links.chat_url?.let { others.addAll(it) }
        coinCD.links.announcement_url?.let { others.addAll(it) }

        binding.rvOtherUrls.adapter = UrlAdapter(others)
        binding.rvOtherUrls.setHasFixedSize(true)

        val repos = ArrayList<String?>()
        for (e in coinCD.links.repos_url) {
            for (s in e.value) {
                repos.add(s)
            }
        }
        binding.rvRepoUrls.adapter = UrlAdapter(repos)
        if (coinCD.links.subreddit_url != null) {
            val linkedText =
                String.format(
                    "<a href=\"%s\">\"%s\"</a> ",
                    coinCD.links.subreddit_url,
                    coinCD.links.subreddit_url
                )
            binding.tvSubreddit.text = Html.fromHtml(linkedText) ?: "NA"
            binding.tvSubreddit.movementMethod = LinkMovementMethod.getInstance()
        }

        binding.tvTwitter.text = coinCD.links.twitter_screen_name


    }

    private fun setupUI() {
        binding.rvHomepageUrls.layoutManager = LinearLayoutManager(context)
        binding.rvBlockchainUrls.layoutManager = LinearLayoutManager(context)
        binding.rvOfficialForumUrls.layoutManager = LinearLayoutManager(context)
        binding.rvOtherUrls.layoutManager = LinearLayoutManager(context)
        binding.rvRepoUrls.layoutManager = LinearLayoutManager(context)

    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            requireActivity(),
            VMFactory(CGApiHelper(RetrofitBuilder.CG_SERVICE))
        ).get(CoinActivityVM::class.java)
    }


}