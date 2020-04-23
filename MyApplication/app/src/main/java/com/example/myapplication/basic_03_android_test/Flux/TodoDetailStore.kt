package com.example.myapplication.basic_03_android_test.Flux

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.util.dagger.ActivityScope
import com.example.myapplication.util.toLiveData
import javax.inject.Inject

@ActivityScope
class TodoDetailStore @Inject constructor(coroutineDispatcher: CoroutineDispatcher) :
    CoroutineStore(coroutineDispatcher), TodoEditingStore {
    override val editingTodo: MediatorLiveData<Todo> by lazy {
        val mediatorLiveData = MediatorLiveData<Todo>()
        mediatorLiveData.addSource(
            dispatcher.subScribe<TodoEdited>()
                .toLiveData(this) {
                    it.todo
                }) {
            mediatorLiveData.value = it
        }
        mediatorLiveData.addSource(
            initializeValue
        ) {
            mediatorLiveData.value = it
        }
        mediatorLiveData
    }

    val initializeValue: LiveData<Todo> by lazy {
        dispatcher.subScribe<DetailedLoaded>()
            .toLiveData(this) {
                it.todo
            }
    }
}