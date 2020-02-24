package com.example.myapplication.sensordetails

import com.example.myapplication.sensordetails.SensorDetailsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface SensorDetailsActivityModule {
    @ContributesAndroidInjector
    fun contributeSensorDetailsFragmentInjector() : SensorDetailsFragment
}