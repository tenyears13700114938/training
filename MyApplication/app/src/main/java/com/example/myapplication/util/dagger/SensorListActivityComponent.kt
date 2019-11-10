package com.example.myapplication.util.dagger

import com.example.myapplication.sensorlist.SensorListActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [SensorListActivityBindingModule::class, SensorListActivityInsModule::class])
@SensorListActivityScope
interface SensorListActivityComponent : AndroidInjector<SensorListActivity> {
    @Subcomponent.Factory
    interface factory : AndroidInjector.Factory<SensorListActivity>
}

annotation class SensorListActivityScope
