package com.example.myapplication.basic_03_android_test.Flux

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoroutineDispatcher @Inject constructor(){
    @ExperimentalCoroutinesApi
    val broadcastChannel = BroadcastChannel<Action>(10)

    @ExperimentalCoroutinesApi
    inline fun <reified T : Action> subScribe(): ReceiveChannel<T> {
        return GlobalScope.produce<T> {
            broadcastChannel.consumeEach {
                Log.d("DebugCoroutine", "action : ${it.toString()}")
                if (it is T) {
                    Log.d("DebugCoroutine", "send...")
                    send(it)
                }
            }
            Log.d("DebugCoroutine", "consume over...")
            /*val action = broadcastChannel.openSubscription().receive()
            Log.d("DebugCoroutine", "action : ${action.toString()}")
            if (action is T) {
                Log.d("DebugCoroutine", "send...")
                send(action)
            }*/
        }

    }

    @ExperimentalCoroutinesApi
    fun dispatch(create: ActionCreate)  {
        MainScope().launch {
            withContext(Dispatchers.Default) {
                broadcastChannel.send(create.run())
            }
        }
    }
}