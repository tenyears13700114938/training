package com.example.myapplication.util.dagger

import com.example.myapplication.MyApplication
import com.example.myapplication.basic_03_android_test.TodoService.TodoOpIntentService
import com.example.myapplication.basic_03_android_test.todoNotification.todoAlarmReceiver
import com.example.myapplication.basic_03_android_test.todoSearch.TodoSearchableFragment
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Component(modules = [AppModule::class, ActivityBindingModule::class, AndroidInjectionModule::class])
@Singleton
interface AppComponent {
    //fun registrationComponent() : TodoListComponent.Factory
    //fun registrationTodoDetailComponent() : TodoDetailComponent.Factory
    fun inject(app: MyApplication)
    fun inject(todoAlarmReceiver: todoAlarmReceiver)
    fun inject(todoOpIntentService: TodoOpIntentService)
    fun inject(todoSearchableFragment: TodoSearchableFragment)
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun myApp(app : MyApplication) : Builder
        fun build() : AppComponent
    }
}