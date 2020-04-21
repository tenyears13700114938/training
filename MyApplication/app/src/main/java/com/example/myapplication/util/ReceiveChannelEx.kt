package com.example.myapplication.util

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.basic_03_android_test.Flux.Action
import com.example.myapplication.basic_03_android_test.Flux.CoroutineStore
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

fun <T : Action, S> ReceiveChannel<T>.toLiveData(store: CoroutineStore, initiaVlaue : S? = null, myReduce: (T) -> S): LiveData<S> {
    val result = MutableLiveData<S>().apply {
        initiaVlaue?.let {
            value = initiaVlaue
        }
    }
    val job = MainScope().launch {
        Log.d("DebugCoroutine", "toLiveData before receive...${this@toLiveData.toString()}")
        for(elemnt in this@toLiveData) {
            result.value = myReduce(elemnt)
            Log.d("DebugCoroutine", "toLiveData after receive...")
        }
    }
    store.addHook { job.cancel() }
    Log.d("DebugCoroutine", "toLivedata result...")
    return result
}
