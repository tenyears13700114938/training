package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun <T> LiveData<T>.getOrAwaitValue(wait : Long, unit : TimeUnit, afterObserve : () -> Unit) : T {
    var value : T? = null
    val countDownLatch = CountDownLatch(1)
    val observer = Observer<T> { t ->
        value = t
        countDownLatch.countDown()
    }
    observeForever(observer)
    try {
        afterObserve.invoke()
        countDownLatch.await(wait, unit)
    }
    catch (e : InterruptedException){
        throw TimeoutException("LiveData is not set")
    }
    finally {
        removeObserver(observer)
    }
    return value as T
}