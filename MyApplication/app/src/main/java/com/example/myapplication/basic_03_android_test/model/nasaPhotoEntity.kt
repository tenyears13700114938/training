package com.example.myapplication.basic_03_android_test.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "nasa_photo")
data class nasaPhotoEntity constructor(
    @PrimaryKey(autoGenerate = true)
    var id : Long,
    var copyright :String,
    var date : String,
    var explanation : String,
    var hdurl : String,
    var media_type : String,
    var service_version : String,
    var title : String,
    var url : String
) : Serializable