package com.example.myapplication.sensorlist

import com.example.myapplication.util.dagger.ActivityScope
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [SensorListActivityBindingModule::class, SensorListActivityInsModule::class])
@ActivityScope
interface SensorListActivityComponent : AndroidInjector<SensorListActivity> {
    @Subcomponent.Factory
    interface factory : AndroidInjector.Factory<SensorListActivity>
}

