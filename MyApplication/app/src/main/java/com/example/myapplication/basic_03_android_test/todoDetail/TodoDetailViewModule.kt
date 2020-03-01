package com.example.myapplication.basic_03_android_test.todoDetail

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import com.example.myapplication.basic_03_android_test.todoList.TodoViewModel
import com.example.myapplication.util.dagger.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class TodoDetailViewModule {
    @Provides
    @ActivityScope
    fun providesTodoDetailViewModel(detailActivity : TodoDetailActivity) : TodoDetailViewModel{
        return ViewModelProviders.of(detailActivity).get(TodoDetailViewModel::class.java)
    }

    @Provides
    @ActivityScope
    fun providesTodoViewModel(detailActivity: TodoDetailActivity) : TodoViewModel {
        return ViewModelProviders.of(detailActivity).get(TodoViewModel::class.java)
    }
}