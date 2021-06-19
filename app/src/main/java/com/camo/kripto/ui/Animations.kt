package com.camo.kripto.ui

import android.view.View

object Animations {
    fun toggleArrow(view: View, isExpanded: Boolean): Boolean {
        return if (isExpanded) {
            view.animate().setDuration(200).rotation(180F)
            true
        } else {
            view.animate().setDuration(200).rotation(0F)
            false
        }
    }
}