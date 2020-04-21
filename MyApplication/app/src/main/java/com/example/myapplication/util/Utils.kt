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
import java.util.*
import android.graphics.Matrix
import android.os.Environment
import androidx.exifinterface.media.ExifInterface
import com.example.myapplication.basic_03_android_test.model.NasaPhoto
import com.example.myapplication.basic_03_android_test.model.NasaPhotoEntity
import java.time.*
import java.time.format.DateTimeFormatter


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

fun getFileDirs(dirName : String, context: Context) : String{
    //return File(context.filesDir.absolutePath, dirName).let {
      return File("/data/data/" + context.packageName + "/files/", dirName).let {
        it.mkdirs()
        it.absolutePath
    }
}

fun getExternalFilesDir(dirName : String, context: Context) : String {
    return File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath + "/" + context.packageName + "/files", dirName).let {
        it.mkdirs()
        it.absolutePath
    }
}

fun convertToFileName(entity : NasaPhotoEntity) : String {
    return entity.date.replace("-", "_") + "_" + entity.url.substringAfterLast("/")
}

fun getNasaPhotoFile(photo : NasaPhotoEntity, context : Context) : File {
    val dir = getExternalFilesDir("NasaPhoto", context)
    val name = convertToFileName(photo)
    return File(dir, name)
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

//dateStr format: yyyy-MM-dd
@RequiresApi(Build.VERSION_CODES.O)
fun getDateStr(dateStr : String, offsetDays : Int) : String {
    val posList = mutableListOf<Int>()
    for(i in 0 until dateStr.length){
        if(dateStr[i] == '-'){
            posList.add(i)
        }
    }

    if (posList.size >= 2) {
        val year = dateStr.substring(0, posList[0]).toInt()
        val month = dateStr.substring(posList[0] + 1, posList[1]).toInt()
        val day = dateStr.substring(posList[1] + 1).toInt()

        val getDate : LocalDate
        var tempDays = offsetDays
        if(offsetDays >= 0){
            getDate = LocalDate.of(year, month, day).plusDays(tempDays.toLong())
        }
        else {
            tempDays = -offsetDays
            getDate = LocalDate.of(year, month, day).minusDays(tempDays.toLong())
        }
        return getDate.format(DateTimeFormatter.ofPattern("uuuu-MM-dd"))
    } else {
        return ""
    }
}

fun mapNasaphotoToEntity(photo : NasaPhoto) : NasaPhotoEntity {
    return NasaPhotoEntity(0,
        if(photo.copyright != null) photo.copyright else "",
        photo.date,
        photo.explanation,
        if(photo.hdurl != null) photo.hdurl else "",
        photo.media_type,
        photo.service_version,
        photo.title,
        photo.url
        )
}

fun mapNasaPhotoEnityToFileName(photoEntity: NasaPhotoEntity) : String {
    return photoEntity.date + "_" + photoEntity.url.substringAfterLast("/")
}