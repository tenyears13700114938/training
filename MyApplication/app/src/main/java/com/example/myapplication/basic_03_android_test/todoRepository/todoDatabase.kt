package com.example.myapplication.basic_03_android_test.todoRepository

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.basic_03_android_test.model.Todo

@Database(entities = [Todo::class], version = 3, exportSchema = false)
abstract class todoDatabase() : RoomDatabase(){
    abstract fun todoDao() : todoDao

    companion object{
        private  var mIns : todoDatabase? = null
        fun getInstance(appContext : Context) : todoDatabase {
            synchronized(todoDatabase::class.java) {
                if (mIns == null) {
                    mIns = databaseBuilder(
                        appContext,
                        todoDatabase::class.java,
                        "todoDatabase"
                    ).addCallback(databaseCb)
                        .addMigrations(migration_2_3)
                        .build()
                }
                return mIns!!
            }
        }
        private var databaseCb = object : RoomDatabase.Callback(){
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                object : AsyncTask<Void, Void, Void>(){
                    override fun doInBackground(vararg params: Void?) : Void? {
                        mIns?.todoDao()?.deleteAll()
                        var todo = Todo(0, "helloworld", false, "android is open for study")
                        mIns?.todoDao()?.insertTodo(todo)
                        todo = Todo(0, "another helloworld", false, "android is good for self learning")
                        mIns?.todoDao()?.insertTodo(todo)
                        return null
                    }
                }.execute()
            }
        }

        private  var migration_1_2 = object  : Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `todo_table` ADD COLUMN `description` TEXT ")
            }
        }

        private  var migration_2_3 = object  : Migration(2,3){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `todo_table` ADD COLUMN `image_url` TEXT ")
            }
        }
    }
}