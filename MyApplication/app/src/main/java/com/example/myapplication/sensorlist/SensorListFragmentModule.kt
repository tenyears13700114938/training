package com.example.myapplication.sensorlist

import android.view.LayoutInflater
import com.example.myapplication.util.dagger.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class SensorListFragmentModule {
    @ActivityScope
    @Provides
    fun providesSensorListAdapter(layoutInflater: LayoutInflater) : SensorListAdapter{
        return SensorListAdapter(layoutInflater)
    }
}