package com.example.myapplication

import android.content.Context
import androidx.test.espresso.idling.CountingIdlingResource
import com.example.myapplication.basic_03_android_test.Flux.TodoAdded
import com.example.myapplication.basic_03_android_test.Flux.TodoDeleted
import com.example.myapplication.basic_03_android_test.Flux.TodoUpdated
import com.example.myapplication.basic_03_android_test.TodoService.ITodoLogic
import com.example.myapplication.basic_03_android_test.TodoService.TodoLogic
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DecoratedTodoLogic @Inject constructor(
    val appContext: Context,
    val todoRepository: todoRepository,
    val countingIdlingResource: CountingIdlingResource
) : ITodoLogic {
    val todoLogic = TodoLogic(appContext, todoRepository)

    override fun isTodoEditing(todo: Todo) = todoLogic.isTodoEditing(todo)

    override fun isTodoAdding(todo: Todo) = todoLogic.isTodoAdding(todo)

    override fun popEditMap(todo: Todo) = todoLogic.popEditMap(todo)

    override suspend fun updateTodo(todo: Todo): TodoUpdated {
        countingIdlingResource.increment()
        try {
            return todoLogic.updateTodo(todo)
        } finally {
            countingIdlingResource.decrement()
        }
    }

    override suspend fun deleteTodo(todo: Todo): TodoDeleted {
        countingIdlingResource.increment()
        try {
            return todoLogic.deleteTodo(todo)
        } finally {
            countingIdlingResource.decrement()
        }
    }

    override suspend fun addTodo(todo: Todo): TodoAdded {
        countingIdlingResource.increment()
        try {
            return todoLogic.addTodo(todo)
        } finally {
            countingIdlingResource.decrement()
        }
    }

    override fun removeTodo(todo: Todo) = todoLogic.removeTodo(todo)
}