package com.example.myapplication.basic_03_android_test.model

import android.view.View
import com.example.myapplication.basic_03_android_test.uiCommon.CardEvent

data class TodoEvent(val cardEvent: CardEvent, val todo: Todo, val refView : View)
