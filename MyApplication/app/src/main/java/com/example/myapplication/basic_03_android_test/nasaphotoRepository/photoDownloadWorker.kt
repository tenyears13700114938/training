package com.example.myapplication.basic_03_android_test.nasaphotoRepository

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myapplication.basic_03_android_test.model.nasaPhotoEntity
import com.example.myapplication.basic_03_android_test.todoList.StartType
import com.example.myapplication.basic_03_android_test.todoList.TodoListActivity
import com.example.myapplication.basic_03_android_test.todoNotification.todoNotificationManager
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import com.example.myapplication.util.convertToFileName
import com.example.myapplication.util.getExternalFilesDir
import com.example.myapplication.util.getFileDirs
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class photoDownloadWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    val photosDir = getExternalFilesDir("nasaPhoto", applicationContext)
    companion object{
        val TAG = photoDownloadWorker::class.java.simpleName
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        //search todo soon expire db or expired
        Log.d(TAG, "doWork......")

        nasaRepository.getAllNasaPhotos(applicationContext)
            .let {
                it.stream().forEach{_photoEntity ->
                    if(_photoEntity.media_type.equals("image")){
                        var target =  File(photosDir, convertToFileName(_photoEntity))
                        if (!target.exists()) {
                            nasaPhotoServiceFactory.getNasaPhotoService()
                                .downloadPhoto(_photoEntity.url)
                                .enqueue(object : Callback<ResponseBody> {
                                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                        Log.d(TAG, "fail...{t}")
                                    }

                                    override fun onResponse(
                                        call: Call<ResponseBody>,
                                        response: Response<ResponseBody>
                                    ) {
                                        Log.d(TAG, "download success...write to file...")
                                        FileOutputStream(target).also {
                                            it.write(response.body()?.bytes())
                                        }
                                    }
                                })
                        }
                    }
                }
            }

        return Result.success()
    }
}