package com.example.myapplication.basic_03_android_test.todoList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoRepository.ITodoRepository
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository

class TodoListViewModel constructor(val app : Application, var type : StartType, val todoRepository: ITodoRepository) : AndroidViewModel(app) {
    val todoList : LiveData<List<Todo>> by lazy {
        when(type) {
            StartType.all -> todoRepository.alltodos
            else -> todoRepository.getLiveNotificationTodo(System.currentTimeMillis())
        }
    }
    var displayType : MutableLiveData<Pair<ListDisplayType, ListDisplayType>> = MutableLiveData(Pair(ListDisplayType.none, ListDisplayType.all))

    /*companion object {
        fun getTodoListViewModel(activity : FragmentActivity, type : StartType) : TodoListViewModel{
            return ViewModelProviders.of(activity, object : ViewModelProvider.Factory{
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return TodoListViewModel(activity.application, type) as T
                }
            }).get(TodoListViewModel::class.java)
        }
    }*/
}

enum class ListDisplayType {
    none,
    all,
    active,
    completed
}