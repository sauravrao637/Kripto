package com.camo.kripto.ui.adapter.tab

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.camo.kripto.ui.presentation.global.FragGlobalCrypto
import com.camo.kripto.ui.presentation.global.FragGlobalDefi

class GlobalActivityTabAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    companion object {
        private const val TOTAL_TABS = 2
    }

    override fun getItemCount(): Int {
        return TOTAL_TABS
    }

    private var list: ArrayList<Fragment> = ArrayList()

    init {
        list.add(0, FragGlobalCrypto())
        list.add(1, FragGlobalDefi())
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

}