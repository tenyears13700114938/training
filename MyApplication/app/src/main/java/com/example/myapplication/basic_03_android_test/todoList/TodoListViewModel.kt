package com.example.myapplication.basic_03_android_test.todoList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository

class TodoListViewModel(app : Application) : AndroidViewModel(app) {
    val todoList : LiveData<List<Todo>> by lazy {
        todoRepository.getInstance(app.applicationContext).alltodos
    }
    var displayType : MutableLiveData<Pair<ListDisplayType, ListDisplayType>> = MutableLiveData(Pair(ListDisplayType.none, ListDisplayType.all))
}

enum class ListDisplayType {
    none,
    all,
    active,
    completed
}