package com.example.myapplication.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import com.example.myapplication.basic_03_android_test.model.Todo
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*


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

@RequiresApi(Build.VERSION_CODES.O)
fun localDateOfTimeFromUtc(millseconds : Long) : LocalDateTime {
    val utcInstant = Date(millseconds).toInstant()
    return  ZonedDateTime.ofInstant(utcInstant, ZoneId.systemDefault()).toLocalDateTime()
}

fun copyTodo(from: Todo, to: Todo) {
    to.thing = from.thing
    to.targetTime = from.targetTime
    to.imageUrl = from.imageUrl
    to.completed = from.completed
    to.description = from.description
    to.comment = from.comment
}
