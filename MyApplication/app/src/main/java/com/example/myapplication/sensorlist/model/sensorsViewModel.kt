package com.example.myapplication.sensorlist.model

import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class sensorsViewModel(val sensorManager: SensorManager) : ViewModel() {
    var sensorInfos = MutableLiveData<List<Sensor>>()

    fun readSensors() : LiveData<List<Sensor>>{
        if(sensorInfos.value == null || sensorInfos.value?.size == 0) {
            viewModelScope.launch {
                sensorInfos.postValue(sensorManager.getSensorList(Sensor.TYPE_ALL))
            }
        }
        return sensorInfos
    }
}