package com.example.stocktracker.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import com.example.stocktracker.R
import com.example.stocktracker.data.repository.StockRepository
import com.example.stocktracker.ui.portfolio.PortfolioActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StockWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) updateWidget(context, appWidgetManager, id)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            WidgetPrefs.deleteScheme(context, id)
            WidgetPrefs.deleteName(context, id)
        }
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
        const val ACTION_REFRESH = "com.example.stocktracker.widget.ACTION_REFRESH"

        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val scheme = WidgetPrefs.getScheme(context, appWidgetId)
            val views = RemoteViews(context.packageName, R.layout.widget_stock)

            // Background (preserves rounded corners via the scheme's drawable)
            views.setInt(R.id.widget_root, "setBackgroundResource", scheme.bgDrawableRes)

            // Header title and colours
            views.setTextViewText(R.id.tv_widget_title, WidgetPrefs.getName(context, appWidgetId))
            views.setTextColor(R.id.tv_widget_title, scheme.titleColor)
            views.setTextColor(R.id.btn_widget_settings, scheme.titleColor)
            views.setTextColor(R.id.btn_widget_refresh, scheme.titleColor)
            views.setTextColor(R.id.tv_widget_empty, scheme.symbolColor)

            // List adapter — embed widget ID so the factory can look up the scheme
            val serviceIntent = Intent(context, StockWidgetService::class.java).also {
                it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                it.data = Uri.parse(it.toUri(Intent.URI_INTENT_SCHEME))
            }
            views.setRemoteAdapter(R.id.lv_widget_stocks, serviceIntent)
            views.setEmptyView(R.id.lv_widget_stocks, R.id.tv_widget_empty)

            // Template pending intent — each item fills in the ACTION_VIEW Uri
            val templateIntent = Intent(Intent.ACTION_VIEW)
            val templateFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE or
                        PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            }
            val templatePi = PendingIntent.getActivity(context, 0, templateIntent, templateFlags)
            views.setPendingIntentTemplate(R.id.lv_widget_stocks, templatePi)

            // Header tap → open app
            val appPi = PendingIntent.getActivity(
                context, 1,
                Intent(context, PortfolioActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.tv_widget_title, appPi)

            // Settings button → open configure screen for this widget
            val configIntent = Intent(context, WidgetConfigActivity::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val configPi = PendingIntent.getActivity(
                context, 3, configIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_widget_settings, configPi)

            // Refresh button
            val refreshPi = PendingIntent.getBroadcast(
                context, 2,
                Intent(context, StockWidgetProvider::class.java).apply { action = ACTION_REFRESH },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_widget_refresh, refreshPi)

            appWidgetManager.updateAppWidget(appWidgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_widget_stocks)
        }

        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, StockWidgetProvider::class.java))
            if (ids.isEmpty()) return
            ids.forEach { updateWidget(context, manager, it) }
            manager.notifyAppWidgetViewDataChanged(ids, R.id.lv_widget_stocks)
        }
    }
}
