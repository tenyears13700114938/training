package com.example.myapplication.basic_03_android_test.todoList

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R


class SimpleRecyclerView : RecyclerView.Adapter<RecyclerView.ViewHolder>(), TodoListItemDecoration.StickyHeaderInterface {
    private var mData  = mutableListOf<Data>()

    init {
        mData.add(Data(1));
        mData.add(Data(0));
        mData.add(Data(0));
        mData.add(Data(0));
        mData.add(Data(0));
        mData.add(Data(0));
        mData.add(Data(2));
        mData.add(Data(0));
        mData.add(Data(0));
        mData.add(Data(0));
        mData.add(Data(0));
        mData.add(Data(0));
        mData.add(Data(0));
        mData.add(Data(1));
        mData.add(Data(0));
        mData.add(Data(0));
        mData.add(Data(0));
        mData.add(Data(0));
        mData.add(Data(0));
        mData.add(Data(0));
        mData.add(Data(2));
        mData.add(Data(0));
        mData.add(Data(0));
        mData.add(Data(0));
        mData.add(Data(0));
        mData.add(Data(0));
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> HeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.header1_item_recycler,
                    parent,
                    false
                )
            )
            2-> Header2ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.header2_item_recycler,
                    parent,
                    false
                )
            )
            else -> ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_recycler,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(@NonNull holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bindData(position)
        } else if (holder is HeaderViewHolder) {
            holder.bindData(position)
        } else if (holder is Header2ViewHolder) {
            holder.bindData(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return mData[position].viewType
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var itemPosition = itemPosition
        var headerPosition = 0
        do {
            if (isHeader(itemPosition)) {
                headerPosition = itemPosition
                break
            }
            itemPosition -= 1
        } while (itemPosition >= 0)
        return headerPosition
    }

    override fun getHeaderLayout(headerPosition: Int): Int {
        return if (mData[headerPosition].viewType == 1) R.layout.header1_item_recycler else {
            R.layout.header2_item_recycler
        }
    }

    override fun bindHeaderData(header: View, headerPosition: Int) {
        header.findViewById<TextView>(R.id.tvHeader).also{
            it.text  = (headerPosition / 5).toString()
        }
    }

    override fun isHeader(itemPosition: Int): Boolean {
        return if (mData[itemPosition].viewType == 1 || mData[itemPosition].viewType == 2) true else false
    }

    internal class HeaderViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        var tvHeader: TextView
        fun bindData(position: Int) {
            tvHeader.text = (position / 5).toString()
        }

        init {
            tvHeader = itemView.findViewById(R.id.tvHeader)
        }
    }

    internal class Header2ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var tvHeader: TextView
        fun bindData(position: Int) {
            tvHeader.text = (position / 5).toString()
        }

        init {
            tvHeader = itemView.findViewById(R.id.tvHeader)
        }
    }

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvRows: TextView
        fun bindData(position: Int) {
            tvRows.text = "saber$position"
            (tvRows.parent as ViewGroup).setBackgroundColor(Color.parseColor("#ffffff"))
        }

        init {
            tvRows = itemView.findViewById(R.id.tvRows)
        }
    }


    internal class Data(var viewType: Int)
}