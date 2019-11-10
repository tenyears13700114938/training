package com.example.myapplication.util.dagger

import android.app.Service
import android.hardware.SensorManager
import android.view.LayoutInflater
import com.example.myapplication.MyApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun providesSensorManager(app : MyApplication) : SensorManager{
        return app.getSystemService(Service.SENSOR_SERVICE) as SensorManager
    }

    @Provides
    @Singleton
    fun providesLayoutInflater(app : MyApplication) : LayoutInflater{
        return LayoutInflater.from(app)
    }
}