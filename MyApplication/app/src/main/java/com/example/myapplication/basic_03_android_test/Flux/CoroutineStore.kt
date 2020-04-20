package com.example.myapplication.basic_03_android_test.Flux

import androidx.lifecycle.ViewModel

open class CoroutineStore(val dispatcher: CoroutineDispatcher) : ViewModel() {
    override fun onCleared() {
        super.onCleared()
    }
}