package com.example.myapplication.basic_03_android_test.nasaphotoRepository

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.paging.DataSource
import com.example.myapplication.basic_03_android_test.model.NasaPhoto
import com.example.myapplication.basic_03_android_test.model.NasaPhotoEntity
import com.example.myapplication.util.getDateStr
import io.reactivex.Observable
import java.util.*

class nasaRepository {
    companion object{
        private val TAG = nasaRepository::class.java.simpleName
        fun getNasaPhoto(date : String, hd : Boolean = false) : Observable<NasaPhoto>{
            return nasaPhotoServiceFactory.getNasaPhotoService().getNasaPhoto(date, hd)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getNasaPhotos(date : String, hd : Boolean = false, size : Int = 1) : Observable<List<NasaPhoto>>{
            val observableList = mutableListOf<Observable<NasaPhoto>>()
            for (i in 0 until size) {
                val getDate = getDateStr(date, -i)
                if (Objects.equals(getDate, "")) {
                    Log.d(TAG, "error date: ${getDate}")
                    continue
                }
                observableList.add(
                    nasaPhotoServiceFactory.getNasaPhotoService().getNasaPhoto(
                        getDate,
                        false
                    )
                )
            }

            val photoList = mutableListOf<NasaPhoto>()
            observableList.forEach { _photoObservable ->
                photoList.add(_photoObservable.blockingFirst())
            }
            return Observable.just(photoList)
        }

        fun getNasaPhotoFromDb(appContext: Context, date: String) : Observable<NasaPhotoEntity> =
            nasaPhotoDatabase.getIns(appContext).nasaPhotoDao().getNasaPhoto(date)

        fun getNasaPhotosFromDb(appContext : Context) : DataSource.Factory<Int, NasaPhotoEntity>{
            return nasaPhotoDatabase.getIns(appContext).nasaPhotoDao().getNasaPhotos()
        }

        fun insertNasaPhotos(appContext: Context, photos : List<NasaPhotoEntity>) {
            nasaPhotoDatabase.getIns(appContext).nasaPhotoDao().insertNasaPhotos(photos)
        }

        fun getAllNasaPhotos(appContext: Context) =
            nasaPhotoDatabase.getIns(appContext).nasaPhotoDao().getAllNasaPhotos()

    }
}