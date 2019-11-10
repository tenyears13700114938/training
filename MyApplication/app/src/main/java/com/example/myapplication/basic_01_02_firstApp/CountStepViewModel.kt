package com.example.myapplication.basic_01_02_firstApp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CountViewModel : ViewModel() {
    var countStep : MutableLiveData<Int> = MutableLiveData(0)

    fun getCountViewModel() : MutableLiveData<Int> {
        return countStep
    }

}