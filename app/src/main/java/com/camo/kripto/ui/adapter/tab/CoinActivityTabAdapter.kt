package com.camo.kripto.ui.adapter.tab
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.camo.kripto.ui.presentation.coin.FragAlerts
import com.camo.kripto.ui.presentation.coin.FragCoinInfo
import com.camo.kripto.ui.presentation.coin.FragPriceChart

class CoinActivityTabAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    companion object{
        private const val TOTAL_TABS = 3
    }

    override fun getItemCount(): Int {
        return TOTAL_TABS
    }
    private var list: ArrayList<Fragment> = ArrayList()

    init {
        list.add(0,FragPriceChart())
        list.add(1,FragCoinInfo())
        list.add(2,FragAlerts())
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

}