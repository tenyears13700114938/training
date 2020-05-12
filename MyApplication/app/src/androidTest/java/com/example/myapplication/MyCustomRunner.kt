package com.example.myapplication

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class MyCustomRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, TestMyApplication::class.java.name, context)
    }
}