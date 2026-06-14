package com.example.stocktracker.ui.portfolio

data class StockDisplayItem(
    val id: Long,
    val name: String,
    val symbol: String,
    val sharesHeld: Double,
    val unitCostGbp: Double,
    val currency: String,
    val showInWidget: Boolean,
    val currentPriceGbp: Double?,
    val changePercent: Double?,
    val totalValueGbp: Double?,
    val totalProfitLossGbp: Double?,
    val isStale: Boolean
)
