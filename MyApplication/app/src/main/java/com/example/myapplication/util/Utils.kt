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
import android.R.attr.scaleHeight
import android.R.attr.scaleWidth
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface


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


fun toBitmapFile(originalFile : String, toWidth : Int, toHeight : Int) : String {
    val sizeOptions = BitmapFactory.Options()
    sizeOptions.inJustDecodeBounds = true

    BitmapFactory.decodeFile(originalFile, sizeOptions)
    var originalWidth = sizeOptions.outWidth
    var originalHeight = sizeOptions.outHeight

    var widthRatio = originalWidth.toFloat() / toWidth.toFloat()
    var heightRatio = originalHeight.toFloat() / toHeight.toFloat()

    var decideWidth = 0
    var decideHeight = 0

    var sampleOptions = BitmapFactory.Options()
    sampleOptions.inJustDecodeBounds = false
    sampleOptions.inSampleSize = if (widthRatio > heightRatio)  widthRatio.toInt() else heightRatio.toInt()
    var scaleBitmap = BitmapFactory.decodeFile(originalFile, sampleOptions)


    val exif = ExifInterface(originalFile)
    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
    var outFileName = originalFile.substringBeforeLast(".") + "_bitmap.jpg"
    var out = File(outFileName).outputStream()

    // createa matrix for the manipulation
    val matrix = Matrix()
    // rotate the Bitmap
    when (orientation) {
        6 -> matrix.postRotate(90f)
        3 -> matrix.postRotate(180f)
        8 -> matrix.postRotate(270f)
    }

    var genBitmap = Bitmap.createBitmap(scaleBitmap, 0, 0, scaleBitmap.width, scaleBitmap.height, matrix, true)

    genBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    out.close()
    genBitmap.recycle()
    scaleBitmap.recycle()
    return outFileName
}
