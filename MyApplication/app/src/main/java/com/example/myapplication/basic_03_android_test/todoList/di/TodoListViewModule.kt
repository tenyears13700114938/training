package com.example.myapplication.basic_03_android_test.todoList.di

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.basic_03_android_test.todoList.StartType
import com.example.myapplication.basic_03_android_test.todoList.TodoListActivity
import com.example.myapplication.basic_03_android_test.todoList.TodoListViewModel
import com.example.myapplication.basic_03_android_test.todoList.TodoViewModel
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import com.example.myapplication.util.dagger.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class TodoListViewModule {
    @Provides
    @ActivityScope
    fun provideTodoListViewModel(activity : TodoListActivity, type : StartType, todoRepository: todoRepository) : TodoListViewModel {
        return ViewModelProviders.of(activity, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return TodoListViewModel(
                    activity.application,
                    type,
                    todoRepository
                ) as T
            }
        }).get(TodoListViewModel::class.java)
    }

    @Provides
    @ActivityScope
    fun provideStartType(/*intent : Intent*/) : StartType {
        //todo
        return StartType.all
        //return if(intent.getIntExtra(TodoListActivity.EXTRA_PARAMETER_START_TYPE, 0) == 1) StartType.search else StartType.all
    }

    @Provides
    @ActivityScope
    fun provideTodoViewModel(activity: TodoListActivity) : TodoViewModel {
        return ViewModelProviders.of(activity).get(TodoViewModel::class.java)
    }
}