package com.example.stocktracker.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.stocktracker.R
import com.example.stocktracker.data.local.StockEntity
import com.example.stocktracker.data.repository.StockRepository
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat
import java.util.Locale

class StockWidgetFactory(
    private val context: Context,
    private val intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private var stocks: List<StockEntity> = emptyList()
    private val repository = StockRepository(context)
    private val gbpFmt = NumberFormat.getCurrencyInstance(Locale.UK)
    private val appWidgetId = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
    )
    private var scheme = WidgetPrefs.getScheme(context, appWidgetId)

    override fun onCreate() = Unit

    override fun onDataSetChanged() {
        stocks = runBlocking { repository.getWidgetStocks() }
        scheme = WidgetPrefs.getScheme(context, appWidgetId)
    }

    override fun onDestroy() = Unit

    override fun getCount(): Int = stocks.size

    override fun getViewAt(position: Int): RemoteViews {
        val stock = stocks[position]
        val rv = RemoteViews(context.packageName, R.layout.widget_stock_item)

        // Row 1: name + price
        rv.setTextViewText(R.id.tv_widget_name, stock.name)
        rv.setTextColor(R.id.tv_widget_name, scheme.nameColor)

        val price = stock.cachedPriceGbp
        val initialValue = stock.unitCostGbp * stock.sharesHeld
        val currentValue = price?.let { it * stock.sharesHeld }
        val pnl = currentValue?.let { it - initialValue }

        rv.setTextViewText(R.id.tv_widget_price, price?.let { gbpFmt.format(it) } ?: "—")
        rv.setTextColor(
            R.id.tv_widget_price,
            if (pnl == null || pnl >= 0) scheme.priceGainColor else scheme.priceLossColor
        )

        // Row 2: symbol | P&L% | current value
        rv.setTextViewText(R.id.tv_widget_symbol, stock.symbol)
        rv.setTextColor(R.id.tv_widget_symbol, scheme.symbolColor)

        val pnlPercent = currentValue?.let { (it - initialValue) / initialValue * 100 }
        val changeText = pnlPercent?.let {
            val sign = if (it >= 0) "+" else ""
            "$sign${"%.1f".format(it)}%"
        } ?: "—"
        rv.setTextViewText(R.id.tv_widget_change, changeText)
        rv.setTextColor(R.id.tv_widget_change,
            if (pnl == null || pnl >= 0) scheme.priceGainColor else scheme.priceLossColor)

        rv.setTextViewText(R.id.tv_widget_total, currentValue?.let { gbpFmt.format(it) } ?: "—")
        rv.setTextColor(R.id.tv_widget_total, scheme.valueColor)

        // Divider colour
        rv.setInt(R.id.widget_item_divider, "setBackgroundColor", scheme.dividerColor)

        // Fill-in intent opens the Yahoo Finance chart for this stock
        val fillIn = Intent().also { it.data = Uri.parse("https://finance.yahoo.com/quote/${stock.symbol}") }
        rv.setOnClickFillInIntent(R.id.widget_item_root, fillIn)

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = stocks[position].id
    override fun hasStableIds(): Boolean = true
}
