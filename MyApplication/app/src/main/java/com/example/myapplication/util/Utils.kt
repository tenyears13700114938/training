package com.example.myapplication.util

import android.view.View
import android.view.ViewGroup


fun searchViewUtil(viewGroup : ViewGroup, viewId : Int) : View? {
    for(childIndex in 0..viewGroup.childCount){
        if(viewGroup.getChildAt(childIndex).id == viewId){
            return viewGroup.getChildAt(childIndex)
        }
        else {
            if(viewGroup.getChildAt(childIndex) is ViewGroup){
                val searchGroup = viewGroup.getChildAt(childIndex) as ViewGroup
                val result = searchViewUtil(searchGroup, viewId)
                if(result != null){
                    return result
                }
            }
        }
    }
    return null
}
