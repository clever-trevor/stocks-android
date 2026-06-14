package com.example.stocktracker.ui.portfolio

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stocktracker.R
import com.example.stocktracker.databinding.ItemStockBinding
import com.google.android.material.card.MaterialCardView
import java.text.NumberFormat
import java.util.Locale

class StockAdapter(
    private val onEdit: (StockDisplayItem) -> Unit,
    private val onDelete: (StockDisplayItem) -> Unit
) : ListAdapter<StockDisplayItem, StockAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<StockDisplayItem>() {
        override fun areItemsTheSame(a: StockDisplayItem, b: StockDisplayItem) = a.id == b.id
        override fun areContentsTheSame(a: StockDisplayItem, b: StockDisplayItem) = a == b
    }

    inner class ViewHolder(private val binding: ItemStockBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val gbpFmt = NumberFormat.getCurrencyInstance(Locale.UK)

        fun bind(item: StockDisplayItem) {
            binding.tvName.text = item.name
            binding.tvSymbol.text = "${item.symbol} · ${item.currency}"
            binding.tvShares.text = "%.4f shares @ ${gbpFmt.format(item.unitCostGbp)}".format(item.sharesHeld)

            val price = item.currentPriceGbp
            if (price != null) {
                binding.tvCurrentPrice.text = gbpFmt.format(price)
                binding.tvTotalValue.text = item.totalValueGbp?.let { "Total: ${gbpFmt.format(it)}" } ?: ""

                val pnl = item.totalProfitLossGbp
                if (pnl != null) {
                    val sign = if (pnl >= 0) "+" else ""
                    val pct = item.changePercent?.let { " (%.2f%%)".format(it) } ?: ""
                    binding.tvProfitLoss.text = "$sign${gbpFmt.format(pnl)}$pct"
                    binding.tvProfitLoss.setTextColor(
                        ContextCompat.getColor(binding.root.context, if (pnl >= 0) R.color.profit_green else R.color.loss_red)
                    )
                } else {
                    binding.tvProfitLoss.text = ""
                }
            } else {
                binding.tvCurrentPrice.text = if (item.isStale) "Pull to refresh" else "Loading…"
                binding.tvTotalValue.text = ""
                binding.tvProfitLoss.text = ""
            }

            // Card stroke colour shows P&L at a glance
            val strokeColor = ContextCompat.getColor(
                binding.root.context,
                when {
                    item.totalProfitLossGbp == null -> R.color.neutral_grey
                    item.totalProfitLossGbp >= 0 -> R.color.profit_green
                    else -> R.color.loss_red
                }
            )
            (binding.root as MaterialCardView).strokeColor = strokeColor

            binding.root.setOnClickListener { onEdit(item) }
            binding.root.setOnLongClickListener { onDelete(item); true }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemStockBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))
}
