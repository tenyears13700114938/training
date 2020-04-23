package com.example.myapplication.basic_03_android_test.todoDetail.di

import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.basic_03_android_test.Flux.TodoDetailStore
import com.example.myapplication.basic_03_android_test.Flux.TodoEditingStore
import com.example.myapplication.basic_03_android_test.Flux.TodoListStore
import com.example.myapplication.basic_03_android_test.todoDetail.TodoDetailActivity
import com.example.myapplication.basic_03_android_test.todoDetail.TodoDetailViewModel
import com.example.myapplication.basic_03_android_test.todoList.TodoViewModel
import com.example.myapplication.util.dagger.ActivityScope
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class TodoDetailViewModule {
    @Module
    companion object {
        @Provides
        @ActivityScope
        @JvmStatic
        fun providesTodoDetailViewModel(detailActivity: TodoDetailActivity): TodoDetailViewModel {
            return ViewModelProviders.of(detailActivity).get(TodoDetailViewModel::class.java)
        }

        @Provides
        @ActivityScope
        @JvmStatic
        fun providesTodoViewModel(detailActivity: TodoDetailActivity): TodoViewModel {
            return ViewModelProviders.of(detailActivity).get(TodoViewModel::class.java)
        }
    }

    @Binds
    @ActivityScope
    abstract fun provideTodoEditingStore(todoListStore : TodoDetailStore) : TodoEditingStore
}