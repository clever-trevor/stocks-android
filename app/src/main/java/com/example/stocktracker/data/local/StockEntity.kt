package com.example.stocktracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocks")
data class StockEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val symbol: String,
    val sharesHeld: Double,
    val unitCostGbp: Double,
    val currency: String,           // "USD", "GBP", or "GBX" (pence)
    val showInWidget: Boolean = false,
    val cachedPriceGbp: Double? = null,
    val cachedChangePercent: Double? = null,
    val cachedPriceTimestamp: Long? = null
)
