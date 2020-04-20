package com.example.myapplication.basic_03_android_test.todoList.di

import com.example.myapplication.basic_03_android_test.todoList.TodoListFragment
import com.example.myapplication.basic_03_android_test.todoList.TodoPhotoEditFragment
import com.example.myapplication.basic_03_android_test.todoList.TodoTimeEditFragment
import com.example.myapplication.basic_03_android_test.todoList.TodoTitleEditFragment
import com.example.myapplication.util.dagger.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface fragmentBindingModule {
    @ContributesAndroidInjector
    fun contributeTodoListFragment() : TodoListFragment

    @ContributesAndroidInjector
    fun contributeTitleEditFragment() : TodoTitleEditFragment

    @ContributesAndroidInjector
    fun contributeTodoTimeEditFragment() : TodoTimeEditFragment

    @ContributesAndroidInjector
    fun contributeTodoPhotoEditFragment() : TodoPhotoEditFragment
}