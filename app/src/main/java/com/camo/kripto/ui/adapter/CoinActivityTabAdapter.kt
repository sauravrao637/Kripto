package com.camo.kripto.ui.adapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.camo.kripto.ui.PriceChart

class CoinActivityTabAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private val TOTAL_TABS = 1

    override fun getItemCount(): Int {
        return TOTAL_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> PriceChart()

            else -> PriceChart()
        }
    }

}