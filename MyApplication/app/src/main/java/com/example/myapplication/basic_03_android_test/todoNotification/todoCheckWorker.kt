package com.example.myapplication.basic_03_android_test.todoNotification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class todoCheckWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        //search todo soon expire db or expired
        return Result.success()
    }
}