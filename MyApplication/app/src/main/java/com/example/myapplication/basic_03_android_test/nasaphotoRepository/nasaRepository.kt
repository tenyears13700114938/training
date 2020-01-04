package com.example.myapplication.basic_03_android_test.nasaphotoRepository

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.paging.DataSource
import com.example.myapplication.basic_03_android_test.model.nasaPhoto
import com.example.myapplication.basic_03_android_test.model.nasaPhotoEntity
import com.example.myapplication.util.getDateStr
import io.reactivex.Observable
import java.util.*

class nasaRepository {
    companion object{
        private val TAG = nasaRepository::class.java.simpleName
        fun getNasaPhoto(date : String, hd : Boolean = false) : Observable<nasaPhoto>{
            return nasaPhotoServiceFactory.getNasaPhotoService().getNasaPhoto(date, hd)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getNasaPhotos(date : String, hd : Boolean = false, size : Int = 1) : Observable<List<nasaPhoto>>{
            val observableList = mutableListOf<Observable<nasaPhoto>>()
            for (i in 0 until size) {
                val getDate = getDateStr(date, -i)
                if (Objects.equals(getDate, "")) {
                    Log.d(TAG, "error date:" + getDate)
                    continue
                }
                observableList.add(
                    nasaPhotoServiceFactory.getNasaPhotoService().getNasaPhoto(
                        getDate,
                        false
                    )
                )
            }

            val photoList = mutableListOf<nasaPhoto>()
            observableList.forEach { _photoObservable ->
                photoList.add(_photoObservable.blockingFirst())
            }
            return Observable.just(photoList)
        }

        fun getNasaPhotosFromDb(appContext : Context) : DataSource.Factory<Int, nasaPhotoEntity>{
            return nasaPhotoDatabase.getIns(appContext).nasaPhotoDao().getNasaPhotos()
        }

        fun insertNasaPhotos(appContext: Context, photos : List<nasaPhotoEntity>) {
            nasaPhotoDatabase.getIns(appContext).nasaPhotoDao().insertNasaPhotos(photos)
        }
    }
}