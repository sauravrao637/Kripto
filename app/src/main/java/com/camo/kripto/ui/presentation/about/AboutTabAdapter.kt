package com.camo.kripto.ui.presentation.about

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AboutTabAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    companion object {
        private const val TOTAL_TABS = 2
    }

    override fun getItemCount(): Int {
        return TOTAL_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragAboutKripto()
            1 -> FragContributors()
            else -> FragContributors()
        }
    }
}