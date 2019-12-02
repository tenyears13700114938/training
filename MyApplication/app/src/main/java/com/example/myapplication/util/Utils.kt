package com.example.myapplication.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import com.example.myapplication.basic_03_android_test.model.Todo
import java.io.File
import java.lang.System.out
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

fun getFileDirs(dirName : String, context: Context) : String{
    //return File(context.filesDir.absolutePath, dirName).let {
      return File("/data/data/" + context.packageName + "/", dirName).let {
        it.mkdirs()
        it.absolutePath
    }
}


fun toBitmapFile(originalFile : String, maxSize : Int) : String {
    return BitmapFactory.decodeFile(originalFile).let{ _originalBitmap ->
        var outFileName = originalFile.substringBeforeLast(".") +  "_bitmap.jpg"
        var out   = File(outFileName).outputStream()
        var width = _originalBitmap.width
        var height = _originalBitmap.height
        var ratio = width.toFloat() / height.toFloat()
        if (ratio > 1){
            width = maxSize
            height = (width.toFloat() / ratio).toInt()
        }
        else {
            height = maxSize
            width = (height.toFloat() * ratio).toInt()
        }
        var genBitmap = Bitmap.createScaledBitmap(_originalBitmap, width, height, true)

        genBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.close()
        outFileName
    }
}
