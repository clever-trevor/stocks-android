package com.example.stocktracker.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.stocktracker.R
import com.example.stocktracker.data.local.StockEntity
import com.example.stocktracker.data.repository.StockRepository
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat
import java.util.Locale

class HoldingsWidgetFactory(
    private val context: Context,
    intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private val appWidgetId = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
    )
    private val repository = StockRepository(context)
    private val gbpFmt = NumberFormat.getCurrencyInstance(Locale.UK)
    private val gbpFmtWhole = NumberFormat.getCurrencyInstance(Locale.UK).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }
    private var stocks: List<StockEntity> = emptyList()

    private val gainColor = Color.parseColor("#4EC77F")
    private val lossColor = Color.parseColor("#F0795F")

    override fun onCreate() = Unit
    override fun onDestroy() = Unit

    override fun onDataSetChanged() {
        stocks = runBlocking { repository.getWidgetStocks() }
    }

    override fun getCount() = stocks.size

    override fun getViewAt(position: Int): RemoteViews {
        val stock = stocks[position]
        val rv = RemoteViews(context.packageName, R.layout.widget_holdings_row)

        val initialValue = stock.unitCostGbp * stock.sharesHeld
        val currentValue = stock.cachedPriceGbp?.let { it * stock.sharesHeld }
        val gain = currentValue?.let { it - initialValue }
        val gainPct = gain?.let { it / initialValue * 100 }
        val gainColor = if (gain == null || gain >= 0) this.gainColor else this.lossColor

        rv.setTextViewText(R.id.tv_hrow_name, stock.name)
        rv.setTextViewText(R.id.tv_hrow_ticker, stock.symbol)
        rv.setTextViewText(R.id.tv_hrow_price,
            stock.cachedPriceGbp?.let { gbpFmt.format(it) } ?: "—")
        rv.setTextViewText(R.id.tv_hrow_value,
            currentValue?.let { gbpFmtWhole.format(it) } ?: "—")
        rv.setTextViewText(R.id.tv_hrow_cost, "from ${gbpFmtWhole.format(initialValue)}")

        val gainText = gain?.let {
            val sign = if (it >= 0) "+" else "−"
            "$sign${gbpFmtWhole.format(kotlin.math.abs(it))}"
        } ?: "—"
        val gainPctText = gainPct?.let {
            val sign = if (it >= 0) "+" else "−"
            "$sign${"%.1f".format(kotlin.math.abs(it))}%"
        } ?: "—"

        rv.setTextViewText(R.id.tv_hrow_gain, gainText)
        rv.setTextViewText(R.id.tv_hrow_gain_pct, gainPctText)
        rv.setTextColor(R.id.tv_hrow_gain, gainColor)
        rv.setTextColor(R.id.tv_hrow_gain_pct, gainColor)

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount() = 1
    override fun getItemId(position: Int) = stocks[position].id
    override fun hasStableIds() = true
}
