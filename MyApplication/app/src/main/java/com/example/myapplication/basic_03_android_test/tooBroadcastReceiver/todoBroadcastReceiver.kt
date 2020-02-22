package com.example.myapplication.basic_03_android_test.tooBroadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.function.Consumer

class todoBroadcastReceiver(val consumer: Consumer<Intent>) : BroadcastReceiver() {

    constructor() : this(object : Consumer<Intent>{
        override fun accept(t: Intent) {
        }
    })

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        consumer.accept(intent)
    }

    companion object {
        val TODO_RESULT_INTENT_FILTER = "Todo_Result"
        val TODO_RESULT_EXTRA_PARAM = "Todo_Result_Extra_Param"
        val TODO_INFO_EXTRA_PARAM = "Todo_Info_Extra_Param"
    }
}
