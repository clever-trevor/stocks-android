package com.example.stocktracker.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.stocktracker.BuildConfig
import com.example.stocktracker.data.local.AppDatabase
import com.example.stocktracker.data.local.StockEntity
import com.example.stocktracker.data.remote.AlphaVantageApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class StockRepository(context: Context) {

    private val stockDao = AppDatabase.getDatabase(context).stockDao()
    private val api = AlphaVantageApi.create()
    private val prefs: SharedPreferences =
        context.getSharedPreferences("stock_prefs", Context.MODE_PRIVATE)
    private val apiKey = BuildConfig.ALPHA_VANTAGE_KEY

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
                val response = api.getGlobalQuote(
                    symbol = toAlphaVantageSymbol(stock.symbol),
                    apiKey = apiKey
                )
                val price = response.globalQuote?.price?.toDoubleOrNull() ?: continue
                val changePercent = response.globalQuote.changePercent
                    ?.replace("%", "")?.trim()?.toDoubleOrNull() ?: 0.0

                val gbpPrice = when (stock.currency.uppercase()) {
                    "USD" -> price * usdToGbp
                    "GBX" -> price / 100.0
                    else -> price
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
            val response = api.getExchangeRate(
                fromCurrency = "USD",
                toCurrency = "GBP",
                apiKey = apiKey
            )
            val rate = response.exchangeRate?.rate?.toDoubleOrNull() ?: 0.79
            prefs.edit()
                .putFloat(KEY_USD_GBP, rate.toFloat())
                .putLong(KEY_USD_GBP_TS, now)
                .apply()
            rate
        } catch (_: Exception) {
            if (cached > 0) cached.toDouble() else 0.79
        }
    }

    private fun toAlphaVantageSymbol(symbol: String): String =
        if (symbol.uppercase().endsWith(".L")) symbol.dropLast(2) + ".LON" else symbol
}
