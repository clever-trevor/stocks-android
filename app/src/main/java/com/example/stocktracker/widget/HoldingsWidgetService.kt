package com.example.stocktracker.widget

import android.content.Intent
import android.widget.RemoteViewsService

class HoldingsWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        HoldingsWidgetFactory(applicationContext, intent)
}
