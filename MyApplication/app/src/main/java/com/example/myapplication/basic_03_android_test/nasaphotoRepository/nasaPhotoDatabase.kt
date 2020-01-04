package com.example.myapplication.basic_03_android_test.nasaphotoRepository

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.basic_03_android_test.model.nasaPhoto
import com.example.myapplication.basic_03_android_test.model.nasaPhotoEntity

@Database(entities = [nasaPhotoEntity::class], version = 1, exportSchema = false)
abstract class nasaPhotoDatabase : RoomDatabase() {
    abstract fun nasaPhotoDao() :nasaPhotoDao

    companion object{
        var mIns : nasaPhotoDatabase? = null
        val databaseCallback = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
            }
        }
        fun getIns(appContext : Context) : nasaPhotoDatabase {
            synchronized(nasaPhotoDatabase::class.java){
                if (mIns == null) {
                    mIns = databaseBuilder(
                        appContext,
                        nasaPhotoDatabase::class.java,
                        "nasaPhotoDatabase"
                    )
                        .addCallback(databaseCallback)
                        .build()
                }
                return mIns!!
            }
        }
    }
}