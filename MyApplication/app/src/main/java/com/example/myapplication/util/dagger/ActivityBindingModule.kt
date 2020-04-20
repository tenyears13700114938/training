package com.example.myapplication.util.dagger

import com.example.myapplication.basic_01_02_firstApp.FirstAppActivity
import com.example.myapplication.basic_01_02_firstApp.FirstAppHomeWorkActivity
import com.example.myapplication.basic_01_02_firstApp.firstAppActivityModule
import com.example.myapplication.basic_01_03_text_scrollingView.ScrollingTextActivity
import com.example.myapplication.basic_02_Activity.IntentReceiveActivity
import com.example.myapplication.basic_02_Activity.IntentSendActivity
import com.example.myapplication.basic_03_android_test.todoDetail.TodoDetailActivity
import com.example.myapplication.basic_03_android_test.todoDetail.di.FragmentBindingModule
import com.example.myapplication.basic_03_android_test.todoDetail.di.TodoDetailViewModule
import com.example.myapplication.basic_03_android_test.todoList.TodoListActivity
import com.example.myapplication.basic_03_android_test.todoList.di.TodoListViewModule
import com.example.myapplication.basic_03_android_test.todoList.di.fragmentBindingModule
import com.example.myapplication.main.MainActivity
import com.example.myapplication.sensordetails.SensorDetailsActivity
import com.example.myapplication.sensordetails.SensorDetailsActivityModule
import com.example.myapplication.sensorlist.SensorListActivityComponent
import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector

@Module(subcomponents = [SensorListActivityComponent::class/*, TodoListComponent::class, TodoDetailComponent::class*/])
interface ActivityBindingModule {
    /*@Binds
    @IntoMap
    @ClassKey(SensorListActivity::class)
    fun bindSensorListActivityInjectorFactory(factory : SensorListActivityComponent.factory) : AndroidInjector.Factory<*>*/

    @ContributesAndroidInjector
    fun contributeMainActivityInjecotr() : MainActivity

    @ContributesAndroidInjector(modules = [firstAppActivityModule::class])
    fun contributeFirstAppActivityInjector() : FirstAppActivity

    @ContributesAndroidInjector
    fun contributeFirstAppHomeworkActivityInjector() : FirstAppHomeWorkActivity

    @ContributesAndroidInjector
    fun contributeScrollingTextActivityInjector() : ScrollingTextActivity

    @ContributesAndroidInjector
    fun contributeIntentSendActivityInjector() : IntentSendActivity

    @ContributesAndroidInjector
    fun contributeIntentReceiveActivityInjector() : IntentReceiveActivity

    @ContributesAndroidInjector(modules = [SensorDetailsActivityModule::class])
    fun contributeSensorDetailsActivity() : SensorDetailsActivity

    @ContributesAndroidInjector(modules = [TodoListViewModule::class, fragmentBindingModule::class, AndroidInjectionModule::class])
    @ActivityScope
    fun contributeTodoListActivity() : TodoListActivity

    @ContributesAndroidInjector(modules = [TodoDetailViewModule::class, AndroidInjectionModule::class, FragmentBindingModule::class])
    @ActivityScope
    fun contributeTodoDetailActivity() : TodoDetailActivity
}