package com.example.myapplication.util.dagger

import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.basic_01_02_firstApp.CountViewModel
import com.example.myapplication.basic_01_02_firstApp.FirstAppActivity
import dagger.Module
import dagger.Provides

@Module
class firstAppActivityModule {
    @Provides
    fun providesCountStepViewModel(activity : FirstAppActivity) : CountViewModel {
        return ViewModelProviders.of(activity).get(CountViewModel::class.java)
    }
}
