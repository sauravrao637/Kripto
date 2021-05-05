package com.camo.kripto.ui.adapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.camo.kripto.ui.CoinInfoFrag
import com.camo.kripto.ui.ExchangesFrag
import com.camo.kripto.ui.PriceChartFrag

class CoinActivityTabAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private val TOTAL_TABS = 2

    override fun getItemCount(): Int {
        return TOTAL_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> PriceChartFrag()
            1-> CoinInfoFrag()
            else -> PriceChartFrag()
        }
    }

}