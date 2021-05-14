package com.camo.kripto.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.camo.kripto.ui.FragGlobalCrypto
import com.camo.kripto.ui.FragGlobalDefi

class GlobalActivityTabAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private val TOTAL_TABS = 2

    override fun getItemCount(): Int {
        return TOTAL_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> FragGlobalCrypto()
            1-> FragGlobalDefi()
            else -> FragGlobalCrypto()
        }
    }

}