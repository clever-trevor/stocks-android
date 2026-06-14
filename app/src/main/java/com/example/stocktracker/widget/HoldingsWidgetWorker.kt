package com.example.stocktracker.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.stocktracker.R
import com.example.stocktracker.data.repository.StockRepository
import java.util.concurrent.TimeUnit

class HoldingsWidgetWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            StockRepository(context).refreshPrices()
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, HoldingsWidgetProvider::class.java)
            )
            if (ids.isNotEmpty()) {
                ids.forEach { HoldingsWidgetProvider.updateWidget(context, manager, it) }
                manager.notifyAppWidgetViewDataChanged(ids, R.id.lv_hw_rows)
            }
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "holdings_widget_refresh"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<HoldingsWidgetWorker>(30, TimeUnit.MINUTES)
                .setConstraints(
                    androidx.work.Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
