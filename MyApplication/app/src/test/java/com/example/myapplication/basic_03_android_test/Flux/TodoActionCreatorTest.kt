package com.example.myapplication.basic_03_android_test.Flux

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.basic_03_android_test.TodoService.ITodoLogic
import com.example.myapplication.basic_03_android_test.TodoService.OpResult
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoRepository.FakeTodoRepository
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TodoActionCreatorTest {
    @InjectMocks
    lateinit var todoActionCreator: TodoActionCreator

    @Mock
    lateinit var todoLogic: ITodoLogic

    @Mock
    lateinit var todoRepository: FakeTodoRepository

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun addTodo() {
        Todo(1, "thing", false).let {
            runBlocking {
                `when`(todoLogic.addTodo(it)).thenReturn(TodoAdded(it, OpResult.ADD_OK))
                todoActionCreator.addTodo(it).create()
                verify(todoLogic, times(1)).addTodo(it)
            }
        }
    }

    @Test
    fun deleteTodo() {
        Todo(2, "thing", false).let {
            runBlocking {
                `when`(todoLogic.deleteTodo(it)).thenReturn(TodoDeleted(it, OpResult.DELETE_OK))
                todoActionCreator.deleteTodo(it).create()
                verify(todoLogic).deleteTodo(it)
            }
        }
    }

    @Test
    fun updateTodo() {
        Todo(3, "thing", true).let {
            runBlocking {
                `when`(todoLogic.updateTodo(it)).thenReturn(TodoUpdated(it, OpResult.UPDATE_OK))
                todoActionCreator.updateTodo(it).create()
                verify(todoLogic).updateTodo(it)
            }
        }
    }

    @Test
    fun editedTodo() {
        Todo(3, "thing", false).let {
            runBlocking {
                val result = todoActionCreator.editedTodo(it).create()
                assertThat(result is TodoEdited, `is`(true))
                val todoEdited = result as TodoEdited
                assertThat(todoEdited.todo, `is`(it))
            }
        }
    }

    @Test
    fun detailLoaded() {
        Todo(4, "thing", false).let {
            runBlocking {
                val result = todoActionCreator.detailLoaded(it).create()
                assertThat(result is DetailedLoaded, `is`(true))
                val detailedLoaded = result as DetailedLoaded
                assertThat(detailedLoaded.todo, `is`(it))
            }
        }
    }

    @Test
    fun getLoadedTodoList() {
        `when`(todoRepository.alltodos).thenReturn(mutableListOf())
        runBlocking {
            val result = todoActionCreator.loadedTodoList.create()
            assertThat(result is TodoListLoaded, `is`(true))
            val list = (result as TodoListLoaded).todoList
            assertThat(list, `is`(mutableListOf()))
        }
    }
}