package com.example.myapplication.basic_03_android_test.todoList

import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TodoListItemDecoration(
    val recyclerView: RecyclerView,
    val stickyHeaderInterface: StickyHeaderInterface
) : RecyclerView.ItemDecoration() {
    var mCurrentHeader: View? = null
    var mCurrentHeaderPos: Int = -1
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val topChild = parent.getChildAt(0)
        if (topChild == null) {
            return
        }
        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return
        }
        val currentHeader = getHeaderViewForItem(topChildPosition, parent)
        //fixLayoutSize(parent, currentHeader)
        val contactPoint = currentHeader.bottom
        val childInContact = getChildInContact(parent, contactPoint)
        if (childInContact == null) {
            return
        }
        if (stickyHeaderInterface.isHeader(parent.getChildAdapterPosition(childInContact))) {
            moveHeader(c, currentHeader, childInContact)
            return
        }
        drawHeader(c, currentHeader)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
    }

    fun getHeaderViewForItem(itemPosition: Int, parent: RecyclerView): View {
        val headerPosition = stickyHeaderInterface.getHeaderPositionForItem(itemPosition)
        if (headerPosition == mCurrentHeaderPos) {
            return mCurrentHeader!!
        }
        val layoutResId = stickyHeaderInterface.getHeaderLayout(headerPosition)
        mCurrentHeader = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        mCurrentHeaderPos = headerPosition
        stickyHeaderInterface.bindHeaderData(mCurrentHeader!!, headerPosition)
        fixLayoutSize(parent, mCurrentHeader!!)
        return mCurrentHeader!!
    }

    fun drawHeader(c: Canvas, header: View) {
        c.save()
        c.translate(0f, 0f)
        header.draw(c)
        c.restore()
    }

    fun moveHeader(c: Canvas, currentHeader: View, nextHeader: View) {
        c.save()
        c.translate(0f, (nextHeader.top - currentHeader.height).toFloat())
        currentHeader.draw(c)
        c.restore()
    }

    fun getChildInContact(parent: RecyclerView, contactPoint: Int): View? {
        var childInContact: View? = null
        for (count in 0..parent.childCount) {
            if (parent.getChildAt(count) != null &&
                parent.getChildAt(count).bottom > contactPoint &&
                parent.getChildAt(count).top <= contactPoint
            ) {
                childInContact = parent.getChildAt(count)
                break
            }
        }
        return childInContact
    }

    fun fixLayoutSize(parent: ViewGroup, view: View) {
        val parentWidth = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val parentHeight =
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        val childWidth = ViewGroup.getChildMeasureSpec(
            parentWidth,
            parent.paddingLeft + parent.paddingRight,
            view.layoutParams.width
        )
        val childHeight = ViewGroup.getChildMeasureSpec(
            parentHeight,
            parent.paddingTop + parent.paddingBottom,
            view.layoutParams.height
        )
        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    interface StickyHeaderInterface {
        fun getHeaderPositionForItem(itemPosition: Int): Int
        fun getHeaderLayout(headerPosition: Int): Int
        fun bindHeaderData(header: View, headerPosition: Int)
        fun isHeader(itemPosition: Int): Boolean
    }
}