package com.example.stocktracker.ui.addstock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.stocktracker.databinding.ActivityAddEditStockBinding

class AddEditStockActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_STOCK_ID = "extra_stock_id"
        fun editIntent(context: Context, id: Long): Intent =
            Intent(context, AddEditStockActivity::class.java).putExtra(EXTRA_STOCK_ID, id)
    }

    private lateinit var binding: ActivityAddEditStockBinding
    private val viewModel: AddEditStockViewModel by viewModels()
    private val currencies = listOf("USD", "GBP", "GBX")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val editId = intent.getLongExtra(EXTRA_STOCK_ID, -1L).takeIf { it >= 0 }
        binding.toolbar.title = if (editId != null) "Edit Stock" else "Add Stock"
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.spinnerCurrency.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
                .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        if (editId != null) viewModel.loadStock(editId)

        viewModel.existingStock.observe(this) { stock ->
            stock ?: return@observe
            binding.etName.setText(stock.name)
            binding.etSymbol.setText(stock.symbol)
            binding.spinnerCurrency.setSelection(currencies.indexOf(stock.currency).coerceAtLeast(0))
            binding.etShares.setText(stock.sharesHeld.toString())
            binding.etUnitCost.setText(stock.unitCostGbp.toString())
            binding.cbWidget.isChecked = stock.showInWidget
        }

        viewModel.saved.observe(this) { if (it) finish() }
        viewModel.error.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        binding.btnSave.setOnClickListener {
            viewModel.saveStock(
                name = binding.etName.text.toString(),
                symbol = binding.etSymbol.text.toString(),
                currency = binding.spinnerCurrency.selectedItem.toString(),
                sharesHeld = binding.etShares.text.toString().toDoubleOrNull() ?: 0.0,
                unitCostGbp = binding.etUnitCost.text.toString().toDoubleOrNull() ?: 0.0,
                showInWidget = binding.cbWidget.isChecked
            )
        }
    }
}
