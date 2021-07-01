package com.camo.kripto.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.customview.widget.ViewDragHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class CustomRV(context: Context, attributeSet: AttributeSet?): RecyclerView(context,attributeSet) {
    constructor(context: Context): this(context,null)
    companion object {
        private const val DIRECTION_VERTICAL = 0
        private const val DIRECTION_HORIZONTAL = 1
        private const val DIRECTION_NO_VALUE = -1
    }

    private var mTouchSlop = 0f
    private var mGestureDirection = 0

    private var mDistanceX = 0f
    private var mDistanceY = 0f
    private var mLastX = 0f
    private var mLastY = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                run {
                    mDistanceX = 0f
                    mDistanceY = mDistanceX
                }
                mLastX = ev.x
                mLastY = ev.y
                mGestureDirection = DIRECTION_NO_VALUE
            }
            MotionEvent.ACTION_MOVE -> {
                val curX = ev.x
                val curY = ev.y
                mDistanceX += abs(curX - mLastX)
                mDistanceY += abs(curY - mLastY)
                mLastX = curX
                mLastY = curY
                mTouchSlop = mDistanceY/mDistanceX
                if(mDistanceX<mDistanceY)return true
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun shouldIntercept(): Boolean {
        if ((mDistanceY > mTouchSlop || mDistanceX > mTouchSlop) && mGestureDirection == DIRECTION_NO_VALUE) {
            mGestureDirection = if (abs(mDistanceY) > abs(mDistanceX)) {
                DIRECTION_VERTICAL
            } else {
                DIRECTION_HORIZONTAL
            }
        }
        return mGestureDirection == ViewDragHelper.DIRECTION_VERTICAL
    }

}