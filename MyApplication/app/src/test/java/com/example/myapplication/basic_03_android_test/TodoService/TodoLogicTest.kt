package com.example.myapplication.basic_03_android_test.TodoService

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoRepository.ITodoRepository
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TodoLogicTest {
    lateinit var testIns: TodoLogic
    lateinit var todoRepository: ITodoRepository

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        System.out.println("setup.....")
        todoRepository = mock(ITodoRepository::class.java)
        testIns =
            TodoLogic(ApplicationProvider.getApplicationContext(), todoRepository)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun isTodoEditing_yes() {
        Todo(1, "thing", false).apply {
            testIns.mEditTodoMap[this.id] = this
            assertThat(testIns.isTodoEditing(this.copy()), `is`(true))
        }
    }

    @Test
    fun isTodoEdittng_no() {
        Todo(1, "thing", false).apply {
            assertThat(testIns.isTodoEditing(this), `is`(false))
        }
    }

    @Test
    fun isTodoAdding_yes() {
        Todo(1, "thing", false).apply {
            testIns.mAddTodoList.add(this)
            assertThat(testIns.isTodoAdding(this.copy()), `is`(true))
        }
    }

    @Test
    fun isTodoAdding_no() {
        Todo(1, "thing", false).apply {
            assertThat(testIns.isTodoAdding(this), `is`(false))
        }
    }

    @Test
    fun popEditMap_existTodo() {
        Todo(1, "thing", false).apply {
            testIns.mEditTodoMap[this.id] = this
            testIns.popEditMap(this)
            assertThat(testIns.mEditTodoMap.contains(this.id), `is`(false))
        }
    }

    @Test
    fun popEditMap_notExitTodo() {
        Todo(1, "thing", false).apply {
            testIns.popEditMap(this)
            assertThat(testIns.mEditTodoMap.contains(this.id), `is`(false))
        }
    }

    @Test
    fun updateTodo_ok() {
        Todo(1, "thing", false).apply {
            runBlocking {
                val result = testIns.updateTodo(this@apply)
                assertThat(result.todo.copy(), `is`(this@apply))
                assertThat(result.result, `is`(OpResult.UPDATE_OK))
                verify(todoRepository).updateToDo(this@apply)
            }
        }
    }

    @Test
    fun updateTodo_alreadyDoing() {
        Todo(1, "thing", false).apply {
            runBlocking {
                testIns.mEditTodoMap[this@apply.id] = this@apply
                val result = testIns.updateTodo(this@apply)
                assertThat(result.todo.copy(), `is`(this@apply))
                assertThat(result.result, `is`(OpResult.TODO_ALREADY_DOING))
            }
        }
    }


    @Test
    fun deleteTodo_ok() {
        Todo(1, "thing", false).apply {
            runBlocking {
                val result = testIns.deleteTodo(this@apply)
                assertThat(result.todo, `is`(this@apply))
                assertThat(result.result, `is`(OpResult.DELETE_OK))
                verify(todoRepository).deleteToDo(this@apply)
            }
        }
    }

    @Test
    fun deleteTodo_alreadyDoing() {
        Todo(1, "thing", false).apply {
            runBlocking {
                testIns.mEditTodoMap[(this@apply).id] = this@apply
                val result = testIns.deleteTodo(this@apply)
                assertThat(this@apply, `is`(result.todo))
                assertThat(result.result, `is`(OpResult.TODO_ALREADY_DOING))
            }
        }
    }

    @Test
    fun addTodo_ok() {
        Todo(1, "thing", false).apply {
            runBlocking {
                val result = testIns.addTodo(this@apply)
                assertThat(result.result, `is`(OpResult.ADD_OK))
                assertThat(result.todo, `is`(this@apply))
                verify(todoRepository).addToDo(this@apply)
            }
        }
    }

    @Test
    fun addTodo_alreadyDoing() {
        Todo(1, "thing", false).apply {
            runBlocking {
                testIns.mAddTodoList.add(this@apply)
                val result = testIns.addTodo(this@apply)
                assertThat(result.result, `is`(OpResult.TODO_ALREADY_DOING))
            }
        }
    }

    @Test
    fun removeTodo() {
        Todo(1, "thing", false).apply {
            testIns.mAddTodoList.add(this)
            testIns.removeTodo(this)
            assertThat(testIns.mAddTodoList.contains(this@apply), `is`(false))
        }
    }
}