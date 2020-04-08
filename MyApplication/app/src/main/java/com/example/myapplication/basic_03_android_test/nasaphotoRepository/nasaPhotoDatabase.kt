package com.example.myapplication.basic_03_android_test.nasaphotoRepository

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.basic_03_android_test.model.NasaPhotoEntity

@Database(entities = [NasaPhotoEntity::class], version = 3, exportSchema = false)
abstract class nasaPhotoDatabase : RoomDatabase() {
    abstract fun nasaPhotoDao() :NasaPhotoDao

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
                        .addMigrations(object : Migration(1, 2){
                            override fun migrate(database: SupportSQLiteDatabase) {
                            }
                        })
                        .addMigrations(object : Migration(2,3){
                            override fun migrate(database: SupportSQLiteDatabase) {
                            }

                        })
                        .addCallback(databaseCallback)
                        .build()
                }
                return mIns!!
            }
        }
    }
}