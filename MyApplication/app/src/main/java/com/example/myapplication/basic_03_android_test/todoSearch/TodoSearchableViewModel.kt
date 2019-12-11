package com.example.myapplication.basic_03_android_test.todoSearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.example.myapplication.basic_03_android_test.model.Todo

class TodoSearchableViewModel : ViewModel() {
    var searchInfo : LiveData<PagedList<Todo>> = MutableLiveData()
}
