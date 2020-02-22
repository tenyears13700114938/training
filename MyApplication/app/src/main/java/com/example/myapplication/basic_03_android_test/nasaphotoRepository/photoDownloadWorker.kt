package com.example.myapplication.basic_03_android_test.nasaphotoRepository

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myapplication.util.convertToFileName
import com.example.myapplication.util.getExternalFilesDir
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class photoDownloadWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    val photosDir = getExternalFilesDir("NasaPhoto", applicationContext)
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
                        val target =  File(photosDir, convertToFileName(_photoEntity))
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
                                        response.body()?.also {_body ->
                                            FileOutputStream(target).write(_body.bytes())
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