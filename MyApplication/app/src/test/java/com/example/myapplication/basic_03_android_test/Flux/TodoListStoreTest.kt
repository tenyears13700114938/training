package com.example.myapplication.basic_03_android_test.Flux

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myapplication.basic_03_android_test.TodoService.OpResult
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.getOrAwaitValue
import com.example.myapplication.util.MainCoroutineRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import java.util.concurrent.TimeUnit

class TodoListStoreTest {
    lateinit var testIns: TodoListStore
    lateinit var dispatcher: CoroutineDispatcher
    @ExperimentalCoroutinesApi
    val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get: Rule
    val secondRule = MainCoroutineRule(testDispatcher)

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        dispatcher = CoroutineDispatcher()
        testIns = TodoListStore(dispatcher)
    }

    @Test
    fun getAddTodoResult() {
        dispatcher.dispatch(ActionCreate {
            TodoAdded(
                Todo(1, "thing", false),
                OpResult.ADD_OK
            )
        })
        assertThat(
            testIns.addTodoResult.getOrAwaitValue(2, TimeUnit.SECONDS) {},
            `is`(OpResult.ADD_OK)
        )
    }

    @Test
    fun getTodoList() {
        val loadList = mutableListOf<Todo>().apply{
            add(Todo(1, "thing", false))
            add(Todo(2, "second", true))
        }
        var todoObserve = testIns.todoList
        dispatcher.dispatch(ActionCreate {
            TodoListLoaded(loadList)
        })
        assertThat(todoObserve.getOrAwaitValue(2, TimeUnit.SECONDS, {}), `is`(loadList as List<Todo>))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testTest(){
        runBlockingTest {
            var a = 0
            launch {
                delay(8000)
                System.out.println("5000 time millis.....")
                a = 1
            }
            launch {
                delay(2000)
                System.out.println("6000 time millis*******")
            }

            launch {
                delay(16000)
                assertThat(a, `is`(1))
            }
        }
    }

    @ExperimentalCoroutinesApi
    @After
    fun teardown(){
        //Dispatchers.resetMain()
        //testDispatcher.cleanupTestCoroutines()
    }
}