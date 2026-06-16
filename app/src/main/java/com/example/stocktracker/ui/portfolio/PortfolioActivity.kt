package com.example.stocktracker.ui.portfolio

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.stocktracker.R
import com.example.stocktracker.databinding.ActivityPortfolioBinding
import com.example.stocktracker.ui.addstock.AddEditStockActivity
import com.example.stocktracker.ui.settings.AppSettingsActivity
import java.text.NumberFormat
import java.util.Locale

class PortfolioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPortfolioBinding
    private val viewModel: PortfolioViewModel by viewModels()
    private val gbpFmt = NumberFormat.getCurrencyInstance(Locale.UK)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPortfolioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val adapter = StockAdapter(
            onEdit = { item ->
                startActivity(AddEditStockActivity.editIntent(this, item.id))
            },
            onDelete = { item ->
                AlertDialog.Builder(this)
                    .setTitle("Remove ${item.name}?")
                    .setPositiveButton("Remove") { _, _ -> viewModel.deleteStock(item) }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddEditStockActivity::class.java))
        }

        viewModel.stocks.observe(this) { items ->
            adapter.submitList(items)
            binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.loading.observe(this) { binding.swipeRefresh.isRefreshing = it }
        viewModel.error.observe(this) { msg ->
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
        viewModel.summary.observe(this) { updateSummary(it) }

        viewModel.refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_portfolio, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, AppSettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateSummary(summary: PortfolioSummary) {
        if (!summary.hasData) {
            binding.tvSummaryTotal.text = "—"
            binding.tvSummaryPnl.text = "—"
            binding.tvSummaryPnlPct.text = ""
            return
        }
        binding.tvSummaryTotal.text = gbpFmt.format(summary.totalValue)
        val sign = if (summary.pnl >= 0) "+" else "−"
        val absAmt = gbpFmt.format(kotlin.math.abs(summary.pnl))
        binding.tvSummaryPnl.text = "$sign$absAmt"
        binding.tvSummaryPnlPct.text = "$sign${"%.2f".format(kotlin.math.abs(summary.pnlPct))}%"
        val color = if (summary.pnl >= 0)
            ContextCompat.getColor(this, R.color.hw_gain)
        else
            ContextCompat.getColor(this, R.color.hw_loss)
        binding.tvSummaryPnl.setTextColor(color)
        binding.tvSummaryPnlPct.setTextColor(color)
    }
}
