package com.example.myapplication.basic_03_android_test.todoList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.util.dagger.ActivityScope
import javax.inject.Inject

class TodoViewModel(app : Application) : AndroidViewModel(app) {
    var todoInfo : Todo = Todo(0, "", false, "")
}