package com.example.myapplication.basic_03_android_test.nasaphotoRepository

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.basic_03_android_test.model.nasaPhotoEntity

@Dao
interface nasaPhotoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNasaPhotos(photos : List<nasaPhotoEntity>)
    @Query("select * from nasa_photo order by date  desc")
    fun getNasaPhotos() : DataSource.Factory<Int, nasaPhotoEntity>
}