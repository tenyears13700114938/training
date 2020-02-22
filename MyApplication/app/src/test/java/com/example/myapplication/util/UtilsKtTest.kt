package com.example.myapplication.util

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.basic_03_android_test.model.NasaPhotoEntity
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class UtilsKtTest {

    @Test
    fun getFileDirs() {
        val dirName = "todoPhoto"
        val context = ApplicationProvider.getApplicationContext() as Context
        val absoluteDirName = getFileDirs(dirName, context)

        val packageName = context.packageName
        assertThat(absoluteDirName, `is`(File("/data/data/$packageName/files/", dirName).absolutePath))
    }

    @Test
    fun getExternalFilesDir() {
        val dirName = "test"
        val context = ApplicationProvider.getApplicationContext() as Context
        val externalDir = com.example.myapplication.util.getExternalFilesDir(dirName, context)

        val tagertFile = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath + "/" + context.packageName + "/files", dirName)

        assertThat(externalDir, `is`(tagertFile.absolutePath))
    }

    @Test
    fun convertToFileName_dataUrl_fileName() {
        val curDate = LocalDate.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));
        val photoUrl = "https://www.google.com/testFile.jpg"
        val photoEntity = NasaPhotoEntity(
            0, "testCopyright", curDate, "", "", "", "", "",
            photoUrl
        )

        val fileName = convertToFileName(photoEntity)

        assertThat(fileName, `is`(curDate.replace("-", "_") + "_" + photoUrl.substringAfterLast("/")))
    }

    @Test
    fun getDateStr_noHalf_empty() {
        assertThat(getDateStr(LocalDate.now().format(DateTimeFormatter.ofPattern("uuuuMMdd")), 1), `is`(""))
    }

    @Test
    fun getDateStr_oneHalf_empty(){
        assertThat(getDateStr(LocalDate.now().format(DateTimeFormatter.ofPattern("uuuu-MMdd")), 1), `is`(""))
    }

    @Test
    fun getDateStr_twoHalfAddOne_plusOne(){
        assertThat(getDateStr(LocalDate.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd")), 1),
            `is`(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("uuuu-MM-dd"))))
    }

    @Test
    fun getDateStr_twohalfMinusOne_minusOne(){
        assertThat(getDateStr(LocalDate.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd")), -1),
            `is`(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("uuuu-MM-dd"))))
    }
}