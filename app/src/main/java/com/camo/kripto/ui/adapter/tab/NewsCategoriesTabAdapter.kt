package com.camo.kripto.ui.adapter.tab

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.camo.kripto.remote.api.CGService
import com.camo.kripto.ui.presentation.home.FragNewsCategory

class NewsCategoriesTabAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    companion object {
        private const val TOTAL_TABS = 8
    }

    override fun getItemCount(): Int {
        return TOTAL_TABS
    }

    private var list: ArrayList<Fragment> = ArrayList()

    init {
        list.add(0, FragNewsCategory.newInstance(CGService.NewsCategory.GENERAL))
        list.add(1, FragNewsCategory.newInstance(CGService.NewsCategory.MILESTONE))
        list.add(2, FragNewsCategory.newInstance(CGService.NewsCategory.PARTNERSHIP))
        list.add(3, FragNewsCategory.newInstance(CGService.NewsCategory.EXCHANGE_LISTING))
        list.add(4, FragNewsCategory.newInstance(CGService.NewsCategory.SOFTWARE_RELEASE))
        list.add(5, FragNewsCategory.newInstance(CGService.NewsCategory.FUND_MOVEMENT))
        list.add(6, FragNewsCategory.newInstance(CGService.NewsCategory.NEW_LISTINGS))
        list.add(7, FragNewsCategory.newInstance(CGService.NewsCategory.EVENT))

    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}