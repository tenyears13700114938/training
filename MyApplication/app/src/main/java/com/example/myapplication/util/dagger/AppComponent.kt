package com.example.myapplication.util.dagger

import com.example.myapplication.MyApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(modules = [AppModule::class, ActivityBindingModule::class, AndroidSupportInjectionModule::class])
@Singleton
interface AppComponent {
    fun inject(app: MyApplication)
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun myApp(app : MyApplication) : Builder
        fun build() : AppComponent
    }
}