package com.example.myapplication.basic_03_android_test.todoList

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.util.dagger.ActivityScope
import dagger.Module
import dagger.Provides
import javax.inject.Scope

@Module
class TodoListViewModule {
    @Provides
    @ActivityScope
    fun provideTodoListViewModel(activity : TodoListActivity, type : StartType) : TodoListViewModel{
        return ViewModelProviders.of(activity, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return TodoListViewModel(activity.application, type) as T
            }
        }).get(TodoListViewModel::class.java)
    }

    @Provides
    @ActivityScope
    fun provideStartType(intent : Intent) : StartType {
        return if(intent.getIntExtra(TodoListActivity.EXTRA_PARAMETER_START_TYPE, 0) == 1) StartType.search else StartType.all
    }

    @Provides
    @ActivityScope
    fun provideTodoViewModel(activity: TodoListActivity) : TodoViewModel{
        return ViewModelProviders.of(activity).get(TodoViewModel::class.java)
    }
}