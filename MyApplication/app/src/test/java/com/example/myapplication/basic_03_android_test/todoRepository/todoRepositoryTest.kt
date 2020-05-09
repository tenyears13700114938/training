package com.example.myapplication.basic_03_android_test.todoRepository

import android.content.pm.ApplicationInfo
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.content.pm.ApplicationInfoBuilder
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.getOrAwaitValue
import org.hamcrest.Matchers.`is`
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class todoRepositoryTest {
    lateinit var testIns: todoRepository
    lateinit var todoDao: todoDao

    @Before
    fun setup() {
        todoDao = mock(FaketodoDao::class.java)
        testIns = todoRepository(ApplicationProvider.getApplicationContext(), todoDao)
    }

    @Test
    fun getAlltodosLiveData() {
        val alltodosLiveData = MutableLiveData<List<Todo>>()
        alltodosLiveData.value = listOf(Todo(1, "thing", true))

        `when`(todoDao.getAllLiveData()).thenReturn(alltodosLiveData)
        assertThat(
            testIns.alltodosLiveData.getOrAwaitValue(2, TimeUnit.SECONDS) {},
            `is`(listOf(Todo(1, "thing", true)))
        )
    }

    @Test
    fun getAlltodos() {
        var allTodos = mutableListOf<Todo>().apply {
            add(Todo(1, "thing", false))
            add(Todo(2, "thing", true))
        }
        `when`(todoDao.getAllTodos()).thenReturn(allTodos)
        assertThat(testIns.alltodos, `is`(allTodos as List<Todo>))
    }

    @Test
    fun addToDo() {
        Todo(1, "thing", false).let {
            testIns.addToDo(it)
            verify(todoDao).insertTodo(it)
        }
    }

    @Test
    fun updateToDo() {
        Todo(1, "thing", true).let {
            testIns.updateToDo(it)
            verify(todoDao).updateTodo(it)
        }
    }

    @Test
    fun deleteToDo() {
        Todo(2, "thing", false).let {
            testIns.deleteToDo(it)
            verify(todoDao).delete(it)
        }
    }

    @Test
    fun searchTodo() {
        testIns.searchTodo("testKey")
        verify(todoDao).searchByTitleOrDescription("testKey")
    }

    @Test
    fun getNotificationTodo() {
        `when`(todoDao.getNotificationTodo(100)).thenReturn(mutableListOf(Todo(1, "todo", true)))
        assertThat(
            testIns.getNotificationTodo(100),
            `is`(mutableListOf(Todo(1, "todo", true)) as List<Todo>)
        )
    }

    @Test
    fun getLiveNotificationTodo() {
        val notificationLiveData = MutableLiveData<List<Todo>>()
        notificationLiveData.value = listOf(Todo(1, "thing", false))

        `when`(todoDao.getLiveNotificationTodo(100)).thenReturn(notificationLiveData)
        assertThat(
            testIns.getLiveNotificationTodo(100).getOrAwaitValue(2, TimeUnit.SECONDS) {},
            `is`(listOf(Todo(1, "thing", false)))
        )
    }
}
