package com.example.myapplication.basic_03_android_test.todoSearch

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository

class TodoSearchableViewModel(val repository : todoRepository) : ViewModel() {
    var searchKey  : MutableLiveData<String> = MutableLiveData()
    fun searchTodo(key: String) {
        searchKey.postValue(key)
    }

    var searchInfo : LiveData<PagedList<Todo>> = Transformations.switchMap(searchKey){
        repository.searchTodo(it)?.let {
            LivePagedListBuilder(it, 20).build()
        }
    }

}
