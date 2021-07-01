package com.camo.kripto.ui.adapter.tab

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.camo.kripto.ui.presentation.coin.FragAlerts
import com.camo.kripto.ui.presentation.coin.FragCoinInfo
import com.camo.kripto.ui.presentation.coin.FragPriceChart
import com.camo.kripto.ui.presentation.home.FragCryptocurrencies
import com.camo.kripto.ui.presentation.home.FragExchanges

class MarketsTabAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    companion object{
        private const val TOTAL_TABS = 2
    }

    override fun getItemCount(): Int {
        return TOTAL_TABS
    }

    private var list: ArrayList<Fragment> = ArrayList()
    init {
        list.add(0, FragCryptocurrencies())
        list.add(1, FragExchanges())
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}