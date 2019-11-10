package com.example.myapplication.util.dagger

import android.view.LayoutInflater
import com.example.myapplication.sensorlist.SensorListAdapter
import dagger.Module
import dagger.Provides

@Module
class SensorListFragmentModule {
    @SensorListActivityScope
    @Provides
    fun providesSensorListAdapter(layoutInflater: LayoutInflater) : SensorListAdapter{
        return SensorListAdapter(layoutInflater)
    }
}