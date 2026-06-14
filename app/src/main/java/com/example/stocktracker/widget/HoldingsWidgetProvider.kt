package com.example.stocktracker.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import com.example.stocktracker.R
import com.example.stocktracker.data.repository.StockRepository
import com.example.stocktracker.ui.portfolio.PortfolioActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HoldingsWidgetProvider : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
        HoldingsWidgetWorker.schedule(context)
    }

    override fun onDisabled(context: Context) {
        HoldingsWidgetWorker.cancel(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (id in appWidgetIds) updateWidget(context, appWidgetManager, id)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH) {
            val pending = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    StockRepository(context).refreshPrices()
                } finally {
                    refreshAll(context)
                    pending.finish()
                }
            }
        }
    }

    companion object {
        const val ACTION_REFRESH = "com.example.stocktracker.holdings.ACTION_REFRESH"

        private val gainColor = Color.parseColor("#4EC77F")
        private val lossColor = Color.parseColor("#F0795F")

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_holdings)

            // Wire the stock-row list
            val serviceIntent = Intent(context, HoldingsWidgetService::class.java).also {
                it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                it.data = Uri.parse(it.toUri(Intent.URI_INTENT_SCHEME))
            }
            views.setRemoteAdapter(R.id.lv_hw_rows, serviceIntent)

            // Item tap → open Yahoo Finance for that ticker
            val templateFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE or
                        PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT
            else
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            views.setPendingIntentTemplate(
                R.id.lv_hw_rows,
                PendingIntent.getActivity(context, 0, Intent(Intent.ACTION_VIEW), templateFlags)
            )

            // Widget tap → open app
            views.setOnClickPendingIntent(
                R.id.tv_hw_title,
                PendingIntent.getActivity(
                    context, 1,
                    Intent(context, PortfolioActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

            // Refresh button
            views.setOnClickPendingIntent(
                R.id.btn_hw_refresh,
                PendingIntent.getBroadcast(
                    context, 10,
                    Intent(context, HoldingsWidgetProvider::class.java).apply {
                        action = ACTION_REFRESH
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

            // Populate header and footer from cached DB data (non-blocking snapshot)
            CoroutineScope(Dispatchers.IO).launch {
                val repo = StockRepository(context)
                val stocks = repo.getWidgetStocks()
                val gbpFmt = NumberFormat.getCurrencyInstance(Locale.UK)

                val totalInitial = stocks.sumOf { it.unitCostGbp * it.sharesHeld }
                val totalCurrent = stocks.sumOf {
                    it.cachedPriceGbp?.let { p -> p * it.sharesHeld } ?: (it.unitCostGbp * it.sharesHeld)
                }
                val totalGain = totalCurrent - totalInitial
                val totalGainPct = if (totalInitial > 0) totalGain / totalInitial * 100 else 0.0
                val isGain = totalGain >= 0
                val color = if (isGain) gainColor else lossColor

                val gainSign = if (isGain) "+" else "−"
                val gainAbs = kotlin.math.abs(totalGain)
                val gainLabel = "$gainSign${gbpFmt.format(gainAbs)} · $gainSign${"%.1f".format(kotlin.math.abs(totalGainPct))}%"

                val timeFmt = SimpleDateFormat("h:mm a", Locale.UK)
                val updatedLabel = "Updated ${timeFmt.format(Date())}"

                withContext(Dispatchers.Main) {
                    views.setTextViewText(R.id.tv_hw_position_count, "${stocks.size} positions")
                    views.setTextViewText(R.id.tv_hw_total, gbpFmt.format(totalCurrent))
                    views.setTextViewText(R.id.tv_hw_total_gain, gainLabel)
                    views.setTextColor(R.id.tv_hw_total_gain, color)
                    views.setTextViewText(R.id.tv_hw_updated, updatedLabel)

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_hw_rows)
                }
            }

            // Push the initial RemoteViews frame immediately (header will fill in async)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, HoldingsWidgetProvider::class.java)
            )
            if (ids.isEmpty()) return
            ids.forEach { updateWidget(context, manager, it) }
            manager.notifyAppWidgetViewDataChanged(ids, R.id.lv_hw_rows)
        }
    }
}
