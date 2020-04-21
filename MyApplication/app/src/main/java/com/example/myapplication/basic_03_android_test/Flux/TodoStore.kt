package com.example.myapplication.basic_03_android_test.Flux

import androidx.lifecycle.LiveData
import com.example.myapplication.basic_03_android_test.TodoService.OpResult
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoList.ListDisplayType
import com.example.myapplication.basic_03_android_test.todoList.StartType
import com.example.myapplication.util.dagger.ActivityScope
import com.example.myapplication.util.toLiveData
import javax.inject.Inject

@ActivityScope
class TodoStore @Inject constructor(dispatcher: CoroutineDispatcher) : CoroutineStore(dispatcher) {

    val addTodoResult: LiveData<OpResult> = dispatcher.subScribe<TodoAdded>()
        .toLiveData(this) {
            it.result
        }

    val todoList: LiveData<List<Todo>> by lazy {
        dispatcher.subScribe<TodoListLoaded>()
            .toLiveData(this) {
                it.todoList
            }
    }

    val updateTodoResult: LiveData<OpResult> =
        dispatcher.subScribe<TodoUpdated>()
            .toLiveData(this) {
                it.result
            }

    val todoListDisplayType: LiveData<ListDisplayType> =
        dispatcher.subScribe<TodoListDisplayType>()
            .toLiveData(this) {
                it.displayType
            }

    val startType: LiveData<StartType> =
        dispatcher.subScribe<StartTypeSelected>()
            .toLiveData(this) {
                it.startType
            }

    val editingTodo: LiveData<Todo> by lazy {
        dispatcher.subScribe<TodoEdited>()
            .toLiveData(this, Todo()) {
                it.todo
            }
    }
}