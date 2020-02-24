package com.example.myapplication.sensorlist

import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.sensorlist.model.sensorsViewModel
import com.example.myapplication.util.dagger.ActivityScope
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class SensorListActivityBindingModule {
    @ContributesAndroidInjector(modules = [SensorListFragmentModule::class])
    abstract fun contributeSensorListFragmentInjector() : SensorListFragment
}

@Module
class SensorListActivityInsModule {
    @Provides
    @ActivityScope
    fun providesSensorsViewModel(activity : SensorListActivity, sensorManager: SensorManager) : sensorsViewModel{
        return activity.run {
            ViewModelProviders.of(activity, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return sensorsViewModel(sensorManager) as T
                }
            }).get(sensorsViewModel::class.java)
        }
    }
}
