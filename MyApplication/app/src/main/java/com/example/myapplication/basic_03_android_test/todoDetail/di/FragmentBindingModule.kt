package com.example.myapplication.basic_03_android_test.todoDetail.di

import com.example.myapplication.basic_03_android_test.todoDetail.TodoDetailFragment
import com.example.myapplication.basic_03_android_test.todoList.TodoPhotoEditFragment
import com.example.myapplication.basic_03_android_test.todoList.TodoTimeEditFragment
import com.example.myapplication.basic_03_android_test.todoList.TodoTitleEditFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FragmentBindingModule {
    @ContributesAndroidInjector
    fun contributeTodoDetailFragment() : TodoDetailFragment

    @ContributesAndroidInjector
    fun contributeTitleEditFragment() : TodoTitleEditFragment

    @ContributesAndroidInjector
    fun contributeTodoTimeEditFragment() : TodoTimeEditFragment

    @ContributesAndroidInjector
    fun contributeTodoPhotoEditFragment() : TodoPhotoEditFragment
}