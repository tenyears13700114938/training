package com.example.myapplication

import com.example.myapplication.util.dagger.ActivityBindingModule
import com.example.myapplication.util.dagger.AppComponent
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [TestAppModule::class, ActivityBindingModule::class, AndroidInjectionModule::class])
interface TestAppComponent : AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun myApp(app: TestMyApplication): Builder
        fun build(): TestAppComponent
    }
}
