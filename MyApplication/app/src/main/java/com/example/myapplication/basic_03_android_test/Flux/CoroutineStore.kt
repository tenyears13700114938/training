package com.example.myapplication.basic_03_android_test.Flux

import androidx.lifecycle.ViewModel

open class CoroutineStore(val dispatcher: CoroutineDispatcher) : ViewModel() {
    val onClearHooks = mutableListOf<() -> Unit>()
    override fun onCleared() {
        super.onCleared()
        onClearHooks.forEach { action -> action.invoke() }
    }

    fun addHook(hook: () -> Unit) = onClearHooks.add(hook)
}