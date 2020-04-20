package com.example.myapplication.basic_03_android_test.Flux

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.util.dagger.ActivityScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@ActivityScope
class TodoStore @Inject constructor(dispatcher: CoroutineDispatcher) : CoroutineStore(dispatcher) {
    inline fun <reified T : Action> ReceiveChannel<T>.toLiveData() : LiveData<T> {
        val result = MutableLiveData<T>()
        MainScope().launch {
            Log.d("DebugCoroutine", "toLiveData before receive...${this@toLiveData.toString()}")
            result.value = this@toLiveData.receive()
            Log.d("DebugCoroutine", "toLiveData after receive...")
        }
        Log.d("DebugCoroutine", "toLivedata result...")
        return result
    }

    val addTodoActioned : LiveData<AddTodoAction> = dispatcher.subScribe<AddTodoAction>()
        .toLiveData()

    val loadTodoListActioned : LiveData<LoadTodoListAction> = dispatcher.subScribe<LoadTodoListAction>()
        .toLiveData()

}