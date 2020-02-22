package com.example.myapplication.basic_03_android_test.nasaphotoRepository

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.basic_03_android_test.model.NasaPhotoEntity
import io.reactivex.Observable

@Dao
interface NasaPhotoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNasaPhotos(photos : List<NasaPhotoEntity>)
    @Query("select * from nasa_photo order by date  desc")
    fun getNasaPhotos() : DataSource.Factory<Int, NasaPhotoEntity>
    @Query("select * from nasa_photo order by date  desc")
    fun getAllNasaPhotos() : List<NasaPhotoEntity>
    @Query("select * from nasa_photo where date = :date")
    fun getNasaPhoto(date : String) : Observable<NasaPhotoEntity>
}