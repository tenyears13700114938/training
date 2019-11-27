package com.example.myapplication.basic_03_android_test.todoDetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.basic_03_android_test.model.Todo

class TodoDetailViewModel(app : Application) : AndroidViewModel(app) {
    var todoDetail : MutableLiveData<Todo> = MutableLiveData(Todo(0, "", false, ""))
}