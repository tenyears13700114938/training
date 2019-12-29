package com.example.myapplication.basic_03_android_test.nasaphotoRepository

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.locks.ReentrantLock

class nasaPhotoServiceFactory {
    companion object{
        private var photoService : nasaPhotoService? = null
        private val entryReentrantLock = ReentrantLock()

        private val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        private val client = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
        }.build()

        private val retrofit = Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/planetary/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()


        fun getNasaPhotoService(): nasaPhotoService {
            entryReentrantLock.lock()
            if (photoService == null) {
                photoService = retrofit.create(nasaPhotoService::class.java)
            }
            entryReentrantLock.unlock()
            return photoService!!
        }
    }
}