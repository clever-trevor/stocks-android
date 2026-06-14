package com.example.stocktracker.widget

import android.content.Intent
import android.widget.RemoteViewsService

class StockWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        StockWidgetFactory(applicationContext, intent)
}
