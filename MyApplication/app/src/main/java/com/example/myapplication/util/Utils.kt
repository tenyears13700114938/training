package com.example.myapplication.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager


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

fun hideSoftInput(activity : Activity){
    val inputManager : InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
}
