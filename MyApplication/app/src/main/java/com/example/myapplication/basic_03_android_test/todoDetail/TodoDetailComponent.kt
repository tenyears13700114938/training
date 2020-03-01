package com.example.myapplication.basic_03_android_test.todoDetail

import com.example.myapplication.util.dagger.ActivityScope
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [TodoDetailViewModule::class])
@ActivityScope
interface TodoDetailComponent {
    @Subcomponent.Factory
    interface  Factory{
        fun create(@BindsInstance activity: TodoDetailActivity) : TodoDetailComponent
    }

    fun inject(activity: TodoDetailActivity)
    fun inject(detailFragment: TodoDetailFragment)
}