package com.example.myapplication.basic_03_android_test.todoList

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoRepository.FakeTodoRepository
import com.example.myapplication.getOrAwaitValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.comparesEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TodoListViewModelTest {
    private lateinit var todoRepository: FakeTodoRepository
    private lateinit var todoListViewModel: TodoListViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        todoRepository = FakeTodoRepository(mutableListOf())
        todoListViewModel = TodoListViewModel(ApplicationProvider.getApplicationContext(), StartType.all, todoRepository)
    }

    @Test
    fun getTodoList_allZeroItem() {
        assertThat(todoListViewModel.todoList.getOrAwaitValue(2, TimeUnit.SECONDS){}.size, comparesEqualTo(0))
    }

    @Test
    fun getTodoList_allMultiItems(){
        todoRepository.addToDo(Todo(1, "test1", false))
        todoRepository.addToDo(Todo(2, "test2", false))
        todoRepository.addToDo(Todo(3, "test3", false))

        assertThat(todoListViewModel.todoList.getOrAwaitValue(2, TimeUnit.SECONDS){}.size, comparesEqualTo(3))
    }

    @Test
    fun getTodoList_expiredZeroItem(){
        todoRepository.addToDo(Todo(1,"test1", false, targetTime = System.currentTimeMillis() + 1000))

        val expiredCount = todoListViewModel.todoList.getOrAwaitValue(2, TimeUnit.SECONDS){}.count { it.targetTime ?: 0 < System.currentTimeMillis() }
        assertThat(expiredCount, comparesEqualTo(0))
    }

    fun getTodoList_expiredMulitiItems(){
        todoRepository.addToDo(Todo(1,"test1", false, targetTime = System.currentTimeMillis() + 1000))
        todoRepository.addToDo(Todo(2,"test2", false, targetTime = System.currentTimeMillis() - 1000))
        todoRepository.addToDo(Todo(3,"test3", false, targetTime = System.currentTimeMillis() - 2000))

        val expiredCount = todoListViewModel.todoList.getOrAwaitValue(2, TimeUnit.SECONDS){}.count{ it.targetTime ?: 0 < System.currentTimeMillis()}
        assertThat(expiredCount, comparesEqualTo(2))
    }

    @Test
    fun getDisplayType_all() {
        val displayTypePair = todoListViewModel.displayType.getOrAwaitValue(2, TimeUnit.SECONDS){}

        assertThat(displayTypePair.first, comparesEqualTo(ListDisplayType.none))
        assertThat(displayTypePair.second, comparesEqualTo(ListDisplayType.all))
    }
}