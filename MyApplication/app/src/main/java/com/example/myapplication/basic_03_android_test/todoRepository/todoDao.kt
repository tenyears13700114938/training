package com.example.myapplication.basic_03_android_test.todoRepository

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myapplication.basic_03_android_test.model.Todo

@Dao
interface todoDao {
    @Query("select * from todo_table")
    fun getAll() : LiveData<List<Todo>>

    @Query("delete from todo_table")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTodo(todo : Todo)

    @Update
    fun updateTodo(todo : Todo)
}