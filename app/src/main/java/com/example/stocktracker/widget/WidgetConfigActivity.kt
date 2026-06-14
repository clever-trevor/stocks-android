package com.example.stocktracker.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocktracker.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class WidgetConfigActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var selectedSchemeId = 0
    private lateinit var nameEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)

        appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContentView(R.layout.activity_widget_config)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        selectedSchemeId = WidgetPrefs.getSchemeId(this, appWidgetId)

        nameEdit = findViewById(R.id.et_widget_name)
        nameEdit.setText(WidgetPrefs.getName(this, appWidgetId))

        val recycler = findViewById<RecyclerView>(R.id.rv_schemes)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = SchemeAdapter(WidgetColorSchemes.all, selectedSchemeId) { id ->
            selectedSchemeId = id
        }

        findViewById<MaterialButton>(R.id.btn_apply).setOnClickListener {
            val name = nameEdit.text.toString().trim().ifEmpty { "My Portfolio" }
            WidgetPrefs.saveName(this, appWidgetId, name)
            WidgetPrefs.saveScheme(this, appWidgetId, selectedSchemeId)
            StockWidgetProvider.updateWidget(this, AppWidgetManager.getInstance(this), appWidgetId)
            setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId))
            finish()
        }
    }

    private inner class SchemeAdapter(
        private val schemes: List<WidgetColorScheme>,
        initialSelected: Int,
        private val onSelect: (Int) -> Unit
    ) : RecyclerView.Adapter<SchemeAdapter.VH>() {

        private var selectedId = initialSelected

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.tv_scheme_name)
            val radio: RadioButton = view.findViewById(R.id.rb_select)
            val previewCard: MaterialCardView = view.findViewById(R.id.cv_preview)
            val previewTitle: TextView = view.findViewById(R.id.tv_preview_title)
            val previewRefresh: TextView = view.findViewById(R.id.tv_preview_refresh)
            val previewDivider0: View = view.findViewById(R.id.v_preview_divider0)
            val previewName1: TextView = view.findViewById(R.id.tv_preview_name1)
            val previewPrice1: TextView = view.findViewById(R.id.tv_preview_price1)
            val previewSymbol1: TextView = view.findViewById(R.id.tv_preview_symbol1)
            val previewChange1: TextView = view.findViewById(R.id.tv_preview_change1)
            val previewTotal1: TextView = view.findViewById(R.id.tv_preview_total1)
            val previewDivider1: View = view.findViewById(R.id.v_preview_divider1)
            val previewName2: TextView = view.findViewById(R.id.tv_preview_name2)
            val previewPrice2: TextView = view.findViewById(R.id.tv_preview_price2)
            val previewSymbol2: TextView = view.findViewById(R.id.tv_preview_symbol2)
            val previewChange2: TextView = view.findViewById(R.id.tv_preview_change2)
            val previewTotal2: TextView = view.findViewById(R.id.tv_preview_total2)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_color_scheme, parent, false)
        )

        override fun getItemCount() = schemes.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val scheme = schemes[position]

            holder.name.text = scheme.displayName
            holder.radio.isChecked = scheme.id == selectedId

            // Preview card background
            holder.previewCard.setCardBackgroundColor(scheme.bgColor)

            // Title bar
            holder.previewTitle.setTextColor(scheme.titleColor)
            holder.previewRefresh.setTextColor(scheme.titleColor)

            // Dividers
            holder.previewDivider0.setBackgroundColor(scheme.dividerColor)
            holder.previewDivider1.setBackgroundColor(scheme.dividerColor)

            // Stock 1 (gain)
            holder.previewName1.setTextColor(scheme.nameColor)
            holder.previewPrice1.setTextColor(scheme.priceGainColor)
            holder.previewSymbol1.setTextColor(scheme.symbolColor)
            holder.previewChange1.setTextColor(scheme.priceGainColor)
            holder.previewTotal1.setTextColor(scheme.valueColor)

            // Stock 2 (loss)
            holder.previewName2.setTextColor(scheme.nameColor)
            holder.previewPrice2.setTextColor(scheme.priceLossColor)
            holder.previewSymbol2.setTextColor(scheme.symbolColor)
            holder.previewChange2.setTextColor(scheme.priceLossColor)
            holder.previewTotal2.setTextColor(scheme.valueColor)

            val click = View.OnClickListener {
                val prevPos = schemes.indexOfFirst { it.id == selectedId }
                selectedId = scheme.id
                notifyItemChanged(prevPos)
                notifyItemChanged(position)
                onSelect(scheme.id)
            }
            holder.itemView.setOnClickListener(click)
            holder.radio.setOnClickListener(click)
        }
    }
}
