package com.camo.kripto.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
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

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> FragCryptocurrencies()
            1-> FragExchanges()
            else -> FragCryptocurrencies()
        }
    }

}