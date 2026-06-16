package com.example.stocktracker.ui.portfolio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.stocktracker.data.local.StockEntity
import com.example.stocktracker.data.repository.StockRepository
import com.example.stocktracker.widget.HoldingsWidgetProvider
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class PortfolioSummary(
    val totalValue: Double,
    val pnl: Double,
    val pnlPct: Double,
    val hasData: Boolean
)

class PortfolioViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = StockRepository(app)

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    val stocks: LiveData<List<StockDisplayItem>> = repository.getAllStocksFlow()
        .map { list -> list.map { it.toDisplayItem() } }
        .asLiveData()

    val summary: LiveData<PortfolioSummary> = repository.getAllStocksFlow()
        .map { list ->
            if (list.isEmpty()) return@map PortfolioSummary(0.0, 0.0, 0.0, false)
            val totalCost = list.sumOf { it.unitCostGbp * it.sharesHeld }
            val totalValue = list.sumOf {
                it.cachedPriceGbp?.let { p -> p * it.sharesHeld } ?: (it.unitCostGbp * it.sharesHeld)
            }
            val pnl = totalValue - totalCost
            val pnlPct = if (totalCost > 0) pnl / totalCost * 100 else 0.0
            PortfolioSummary(totalValue, pnl, pnlPct, true)
        }
        .asLiveData()

    fun refresh() {
        _loading.value = true
        viewModelScope.launch {
            try {
                repository.refreshPrices()
                HoldingsWidgetProvider.refreshAll(getApplication())
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Refresh failed: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteStock(item: StockDisplayItem) {
        viewModelScope.launch {
            repository.getById(item.id)?.let { repository.deleteStock(it) }
        }
    }

    private fun StockEntity.toDisplayItem(): StockDisplayItem {
        val total = cachedPriceGbp?.let { it * sharesHeld }
        val cost = unitCostGbp * sharesHeld
        val pnl = total?.let { it - cost }
        val now = System.currentTimeMillis()
        val isStale = cachedPriceTimestamp == null || now - cachedPriceTimestamp > 900_000L
        return StockDisplayItem(
            id = id,
            name = name,
            symbol = symbol,
            sharesHeld = sharesHeld,
            unitCostGbp = unitCostGbp,
            currency = currency,
            showInWidget = showInWidget,
            currentPriceGbp = cachedPriceGbp,
            changePercent = cachedChangePercent,
            totalValueGbp = total,
            totalProfitLossGbp = pnl,
            isStale = isStale
        )
    }
}
