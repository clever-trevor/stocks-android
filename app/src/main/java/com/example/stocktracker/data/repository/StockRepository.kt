package com.example.stocktracker.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.stocktracker.data.local.AppDatabase
import com.example.stocktracker.data.local.StockEntity
import com.example.stocktracker.data.remote.YahooFinanceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class StockRepository(context: Context) {

    private val stockDao = AppDatabase.getDatabase(context).stockDao()
    private val api = YahooFinanceApi.create()
    private val prefs: SharedPreferences =
        context.getSharedPreferences("stock_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USD_GBP = "usd_gbp_rate"
        private const val KEY_USD_GBP_TS = "usd_gbp_timestamp"
        private const val FOREX_CACHE_MS = 3_600_000L
        private const val PRICE_CACHE_MS = 900_000L
    }

    fun getAllStocksFlow(): Flow<List<StockEntity>> = stockDao.getAllFlow()

    suspend fun getAllStocks(): List<StockEntity> = withContext(Dispatchers.IO) { stockDao.getAll() }

    suspend fun getWidgetStocks(): List<StockEntity> = withContext(Dispatchers.IO) { stockDao.getWidgetStocks() }

    suspend fun addStock(stock: StockEntity): Long = withContext(Dispatchers.IO) { stockDao.insert(stock) }

    suspend fun updateStock(stock: StockEntity) = withContext(Dispatchers.IO) { stockDao.update(stock) }

    suspend fun deleteStock(stock: StockEntity) = withContext(Dispatchers.IO) { stockDao.delete(stock) }

    suspend fun getById(id: Long): StockEntity? = withContext(Dispatchers.IO) { stockDao.getById(id) }

    suspend fun refreshPrices() {
        val stocks = withContext(Dispatchers.IO) { stockDao.getAll() }
        val now = System.currentTimeMillis()
        val usdToGbp = getUsdToGbpRate()

        for (stock in stocks) {
            val isStale = stock.cachedPriceTimestamp == null ||
                    now - stock.cachedPriceTimestamp > PRICE_CACHE_MS
            if (!isStale) continue

            try {
                val meta = api.getChart(toYahooSymbol(stock.symbol))
                    .chart?.result?.firstOrNull()?.meta ?: continue
                val price = meta.regularMarketPrice ?: continue
                val changePercent = meta.regularMarketChangePercent ?: 0.0

                // Yahoo Finance returns currency in the meta: "GBp" = pence, "GBP" = pounds, "USD" = dollars
                val gbpPrice = when (meta.currency) {
                    "GBp" -> price / 100.0
                    "USD" -> price * usdToGbp
                    "GBP" -> price
                    else -> {
                        // Fallback: infer from stored currency/symbol
                        val sym = stock.symbol.uppercase()
                        when {
                            stock.currency.uppercase() == "GBX" ||
                                sym.endsWith(".L") || sym.endsWith(".LON") -> price / 100.0
                            stock.currency.uppercase() == "USD" -> price * usdToGbp
                            else -> price
                        }
                    }
                }

                withContext(Dispatchers.IO) {
                    stockDao.update(
                        stock.copy(
                            cachedPriceGbp = gbpPrice,
                            cachedChangePercent = changePercent,
                            cachedPriceTimestamp = now
                        )
                    )
                }
            } catch (_: Exception) {}
        }
    }

    private suspend fun getUsdToGbpRate(): Double {
        val now = System.currentTimeMillis()
        val cached = prefs.getFloat(KEY_USD_GBP, 0f)
        val timestamp = prefs.getLong(KEY_USD_GBP_TS, 0L)

        if (cached > 0 && now - timestamp < FOREX_CACHE_MS) return cached.toDouble()

        return try {
            // GBPUSD=X: 1 GBP = X USD (e.g. 1.27), so USD→GBP = 1/rate
            val gbpUsd = api.getChart("GBPUSD=X")
                .chart?.result?.firstOrNull()?.meta?.regularMarketPrice ?: return cached.toDoubleOrFallback()
            val rate = 1.0 / gbpUsd
            prefs.edit()
                .putFloat(KEY_USD_GBP, rate.toFloat())
                .putLong(KEY_USD_GBP_TS, now)
                .apply()
            rate
        } catch (_: Exception) {
            cached.toDoubleOrFallback()
        }
    }

    private fun Float.toDoubleOrFallback() = if (this > 0) toDouble() else 0.79

    // Yahoo Finance uses .L for LSE stocks; convert .LON if previously stored that way
    private fun toYahooSymbol(symbol: String): String =
        if (symbol.uppercase().endsWith(".LON")) symbol.dropLast(4) + ".L" else symbol
}
