package com.example.myapplication.basic_03_android_test.nasaphotoRepository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.myapplication.basic_03_android_test.model.nasaPhoto
import io.reactivex.Observable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class nasaRepository {
    companion object{
        private val TAG = nasaRepository::class.java.simpleName
        fun getNasaPhoto(date : String, hd : Boolean = false) : Observable<nasaPhoto>{
            return nasaPhotoServiceFactory.getNasaPhotoService().getNasaPhoto(date, hd)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getNasaPhotos(date : String, hd : Boolean = false, size : Int = 1) : Observable<List<nasaPhoto>>{
            var posLsit = mutableListOf<Int>()
            for(i in 0 until date.length){
                if(date[i] == '-'){
                    posLsit.add(i)
                }
            }

            var parsedStrs = mutableListOf<String>()
            if(posLsit.size >= 2){
                var year = date.substring(0, posLsit[0]).toInt()
                var month = date.substring(posLsit[0] + 1, posLsit[1]).toInt()
                var day = date.substring(posLsit[1] + 1).toInt()

                var observableList = mutableListOf<Observable<nasaPhoto>>()
                for(i in 0 until size){
                    var getDate = LocalDate.of(year, month, day).minusDays(i.toLong())

                    observableList.add(
                        nasaPhotoServiceFactory.getNasaPhotoService().getNasaPhoto(
                            getDate.format(DateTimeFormatter.ofPattern("uuuu-MM-dd")),
                            false
                        )
                    )
                }

                var photoList = mutableListOf<nasaPhoto>()
                observableList.forEach { _photoObservable ->
                    photoList.add(_photoObservable.blockingFirst())


                }
                return Observable.just(photoList)
            }
            else {
                Log.d(TAG, "error date:" + date)
                return Observable.empty()
            }
        }
    }
}