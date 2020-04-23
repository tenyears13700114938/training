package com.example.myapplication.basic_03_android_test.Flux

import androidx.lifecycle.LiveData
import com.example.myapplication.basic_03_android_test.model.Todo

interface TodoEditingStore {
    val editingTodo : LiveData<Todo>
}