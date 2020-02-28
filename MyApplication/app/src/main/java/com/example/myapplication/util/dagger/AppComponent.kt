package com.example.myapplication.util.dagger

import com.example.myapplication.MyApplication
import com.example.myapplication.basic_03_android_test.todoList.TodoListComponent
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(modules = [AppModule::class, ActivityBindingModule::class, AndroidInjectionModule::class])
@Singleton
interface AppComponent {
    fun registrationComponent() : TodoListComponent.Factory
    fun inject(app: MyApplication)
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun myApp(app : MyApplication) : Builder
        fun build() : AppComponent
    }
}