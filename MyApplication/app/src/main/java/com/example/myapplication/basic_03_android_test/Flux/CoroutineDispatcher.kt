package com.example.myapplication.basic_03_android_test.Flux

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoroutineDispatcher @Inject constructor() {
    @ExperimentalCoroutinesApi
    val broadcastChannel = BroadcastChannel<Action>(10)

    @ExperimentalCoroutinesApi
    inline fun <reified T : Action> subScribe(): ReceiveChannel<T> {
        return MainScope().produce<T> {
            broadcastChannel.consumeEach {
                Log.d("DebugCoroutine", "action : ${it.toString()}")
                if (it is T) {
                    Log.d("DebugCoroutine", "send...")
                    send(it)
                }
            }
            Log.d("DebugCoroutine", "consume over...")
        }

    }

    @ExperimentalCoroutinesApi
    fun dispatch(create: ActionCreate) {
        MainScope().launch {
            withContext(Dispatchers.Default) {
                broadcastChannel.send(create.run())
            }
        }
    }
}