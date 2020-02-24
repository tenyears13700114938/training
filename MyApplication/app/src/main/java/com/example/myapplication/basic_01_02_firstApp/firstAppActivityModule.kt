package com.example.myapplication.basic_01_02_firstApp

import androidx.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides

@Module
class firstAppActivityModule {
    @Provides
    fun providesCountStepViewModel(activity : FirstAppActivity) : CountViewModel {
        return ViewModelProviders.of(activity).get(CountViewModel::class.java)
    }
}
