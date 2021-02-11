package com.example.myapplication.notif

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationSchedule (var context: Context, var params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val data = params.inputData
        val title = data.getString("title")
        val body = data.getString("body")

        if (body != null) {
            TriggerNotification(context, title!!, body)
        }

        return Result.success()
    }
}