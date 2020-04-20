package com.example.myapplication.basic_03_android_test.todoDetail.di

import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.basic_03_android_test.todoDetail.TodoDetailActivity
import com.example.myapplication.basic_03_android_test.todoDetail.TodoDetailViewModel
import com.example.myapplication.basic_03_android_test.todoList.TodoViewModel
import com.example.myapplication.util.dagger.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class TodoDetailViewModule {
    @Provides
    @ActivityScope
    fun providesTodoDetailViewModel(detailActivity : TodoDetailActivity) : TodoDetailViewModel {
        return ViewModelProviders.of(detailActivity).get(TodoDetailViewModel::class.java)
    }

    @Provides
    @ActivityScope
    fun providesTodoViewModel(detailActivity: TodoDetailActivity) : TodoViewModel {
        return ViewModelProviders.of(detailActivity).get(TodoViewModel::class.java)
    }
}