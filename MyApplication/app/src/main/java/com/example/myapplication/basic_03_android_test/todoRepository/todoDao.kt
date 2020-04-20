package com.example.myapplication.basic_03_android_test.todoRepository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.model.TodoSearch

@Dao
interface todoDao {
    @Query("select * from todo_table")
    fun getAllLiveData() : LiveData<List<Todo>>

    @Query("select * from todo_table")
    fun getAllTodos() : List<Todo>

    @Query("delete from todo_table")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTodo(todo : Todo)

    @Update
    fun updateTodo(todo : Todo)

    @Delete
    fun delete(todo :Todo)

    @Transaction @Query("select * from todo_table JOIN TodoSearch ON(todo_table.id = TodoSearch.docid) where TodoSearch match :key")
    fun searchByTitleOrDescription(key : String) : DataSource.Factory<Int, Todo>

    @Query("select * from todo_table where target_time != 0 and target_time - 24*60*60*1000 < :millisecs")
    fun getNotificationTodo(millisecs : Long) : List<Todo>

    @Query("select * from todo_table where target_time != 0 and target_time - 24*60*60*1000 < :millisecs")
    fun getLiveNotificationTodo(millisecs : Long) : LiveData<List<Todo>>
}