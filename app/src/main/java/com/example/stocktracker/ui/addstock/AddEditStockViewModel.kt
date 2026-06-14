package com.example.stocktracker.ui.addstock

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.stocktracker.data.local.StockEntity
import com.example.stocktracker.data.repository.StockRepository
import com.example.stocktracker.widget.StockWidgetProvider
import kotlinx.coroutines.launch

class AddEditStockViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = StockRepository(app)

    val saved = MutableLiveData(false)
    val error = MutableLiveData<String?>()
    val existingStock = MutableLiveData<StockEntity?>()

    private var editingId: Long? = null

    fun loadStock(id: Long) {
        editingId = id
        viewModelScope.launch {
            existingStock.postValue(repository.getById(id))
        }
    }

    fun saveStock(
        name: String,
        symbol: String,
        currency: String,
        sharesHeld: Double,
        unitCostGbp: Double,
        showInWidget: Boolean
    ) {
        if (name.isBlank() || symbol.isBlank()) {
            error.value = "Name and symbol are required"
            return
        }
        val current = existingStock.value
        viewModelScope.launch {
            val stock = StockEntity(
                id = editingId ?: 0,
                name = name.trim(),
                symbol = symbol.trim().uppercase(),
                currency = currency,
                sharesHeld = sharesHeld,
                unitCostGbp = unitCostGbp,
                showInWidget = showInWidget,
                // Preserve cache unless symbol changed
                cachedPriceGbp = if (current?.symbol == symbol.trim().uppercase()) current?.cachedPriceGbp else null,
                cachedChangePercent = if (current?.symbol == symbol.trim().uppercase()) current?.cachedChangePercent else null,
                cachedPriceTimestamp = if (current?.symbol == symbol.trim().uppercase()) current?.cachedPriceTimestamp else null
            )
            if (editingId != null) repository.updateStock(stock) else repository.addStock(stock)
            StockWidgetProvider.refreshAll(getApplication())
            saved.postValue(true)
        }
    }
}
