package com.example.myapplication.util.dagger

import com.example.myapplication.basic_01_02_firstApp.FirstAppActivity
import com.example.myapplication.basic_01_02_firstApp.FirstAppHomeWorkActivity
import com.example.myapplication.basic_01_03_text_scrollingView.ScrollingTextActivity
import com.example.myapplication.basic_02_Activity.IntentReceiveActivity
import com.example.myapplication.basic_02_Activity.IntentSendActivity
import com.example.myapplication.main.MainActivity
import com.example.myapplication.sensordetails.SensorDetailsActivity
import com.example.myapplication.sensorlist.SensorListActivity
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [SensorListActivityComponent::class])
interface ActivityBindingModule {
    @Binds
    @IntoMap
    @ClassKey(SensorListActivity::class)
    fun bindSensorListActivityInjectorFactory(factory : SensorListActivityComponent.factory) : AndroidInjector.Factory<*>

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
}