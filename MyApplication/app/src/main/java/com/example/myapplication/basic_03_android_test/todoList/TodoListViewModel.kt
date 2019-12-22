package com.example.myapplication.basic_03_android_test.todoList

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository

class TodoListViewModel(app : Application,  var type : StartType) : AndroidViewModel(app) {
    val todoList : LiveData<List<Todo>> by lazy {
        when(type) {
            StartType.all -> todoRepository.getInstance(app.applicationContext).alltodos
            else -> todoRepository.getInstance(app.applicationContext).getLiveNotificationTodo(System.currentTimeMillis())
        }
    }
    var displayType : MutableLiveData<Pair<ListDisplayType, ListDisplayType>> = MutableLiveData(Pair(ListDisplayType.none, ListDisplayType.all))

    companion object {
        fun getTodoListViewModel(activity : FragmentActivity, type : StartType) : TodoListViewModel{
            return ViewModelProviders.of(activity, object : ViewModelProvider.Factory{
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return TodoListViewModel(activity.application, type) as T
                }
            }).get(TodoListViewModel::class.java)
        }
    }
}

enum class ListDisplayType {
    none,
    all,
    active,
    completed
}