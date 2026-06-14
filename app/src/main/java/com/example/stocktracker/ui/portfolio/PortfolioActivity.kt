package com.example.stocktracker.ui.portfolio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.stocktracker.databinding.ActivityPortfolioBinding
import com.example.stocktracker.ui.addstock.AddEditStockActivity

class PortfolioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPortfolioBinding
    private val viewModel: PortfolioViewModel by viewModels()

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

        viewModel.refresh()
    }
}
