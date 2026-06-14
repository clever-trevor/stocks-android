package com.example.stocktracker.widget

import android.graphics.Color
import com.example.stocktracker.R

data class WidgetColorScheme(
    val id: Int,
    val displayName: String,
    val bgColor: Int,
    val bgDrawableRes: Int,
    val titleColor: Int,
    val nameColor: Int,
    val priceGainColor: Int,
    val priceLossColor: Int,
    val symbolColor: Int,
    val valueColor: Int,
    val dividerColor: Int
)

object WidgetColorSchemes {

    private fun c(hex: String): Int = Color.parseColor(hex)

    val all: List<WidgetColorScheme> = listOf(
        // Deep navy — the original style
        WidgetColorScheme(
            id = 0, displayName = "Ocean",
            bgColor = c("#0D1B3E"), bgDrawableRes = R.drawable.widget_bg_ocean,
            titleColor = c("#FFFFFF"), nameColor = c("#FFFFFF"),
            priceGainColor = c("#4CAF50"), priceLossColor = c("#F44336"),
            symbolColor = c("#AAFFFFFF"), valueColor = c("#CCFFFFFF"),
            dividerColor = c("#33FFFFFF")
        ),
        // Jet black with neon accents
        WidgetColorScheme(
            id = 1, displayName = "Midnight",
            bgColor = c("#0A0A0A"), bgDrawableRes = R.drawable.widget_bg_midnight,
            titleColor = c("#FFFFFF"), nameColor = c("#FFFFFF"),
            priceGainColor = c("#00E676"), priceLossColor = c("#FF1744"),
            symbolColor = c("#99FFFFFF"), valueColor = c("#CCFFFFFF"),
            dividerColor = c("#1AFFFFFF")
        ),
        // Dark forest green with mint/orange
        WidgetColorScheme(
            id = 2, displayName = "Forest",
            bgColor = c("#102018"), bgDrawableRes = R.drawable.widget_bg_forest,
            titleColor = c("#E8F5E9"), nameColor = c("#E8F5E9"),
            priceGainColor = c("#69F0AE"), priceLossColor = c("#FF6D00"),
            symbolColor = c("#99A5D6A7"), valueColor = c("#CCE8F5E9"),
            dividerColor = c("#22A5D6A7")
        ),
        // Warm burgundy with amber/coral
        WidgetColorScheme(
            id = 3, displayName = "Sunset",
            bgColor = c("#2C0F0F"), bgDrawableRes = R.drawable.widget_bg_sunset,
            titleColor = c("#FFE0B2"), nameColor = c("#FFE0B2"),
            priceGainColor = c("#FFAB40"), priceLossColor = c("#FF5252"),
            symbolColor = c("#99FFCCBC"), valueColor = c("#CCFFE0B2"),
            dividerColor = c("#22FF8A65")
        ),
        // Deep purple with lavender/pink
        WidgetColorScheme(
            id = 4, displayName = "Purple Rain",
            bgColor = c("#150A2C"), bgDrawableRes = R.drawable.widget_bg_purple_rain,
            titleColor = c("#EDE7F6"), nameColor = c("#EDE7F6"),
            priceGainColor = c("#CE93D8"), priceLossColor = c("#FF80AB"),
            symbolColor = c("#99CE93D8"), valueColor = c("#CCEDE7F6"),
            dividerColor = c("#22CE93D8")
        ),
        // Blue-grey with teal/coral
        WidgetColorScheme(
            id = 5, displayName = "Slate",
            bgColor = c("#1E272E"), bgDrawableRes = R.drawable.widget_bg_slate,
            titleColor = c("#ECEFF1"), nameColor = c("#ECEFF1"),
            priceGainColor = c("#4DB6AC"), priceLossColor = c("#EF5350"),
            symbolColor = c("#99B0BEC5"), valueColor = c("#CCECEFF1"),
            dividerColor = c("#22B0BEC5")
        ),
        // Dark navy with cyan highlights
        WidgetColorScheme(
            id = 6, displayName = "Arctic",
            bgColor = c("#0A1929"), bgDrawableRes = R.drawable.widget_bg_arctic,
            titleColor = c("#E0F7FA"), nameColor = c("#E0F7FA"),
            priceGainColor = c("#26C6DA"), priceLossColor = c("#EF5350"),
            symbolColor = c("#9980DEEA"), valueColor = c("#CCE0F7FA"),
            dividerColor = c("#2280DEEA")
        ),
        // Dark amber with gold/orange
        WidgetColorScheme(
            id = 7, displayName = "Gold Rush",
            bgColor = c("#1A1200"), bgDrawableRes = R.drawable.widget_bg_gold_rush,
            titleColor = c("#FFF8E1"), nameColor = c("#FFF8E1"),
            priceGainColor = c("#FFD740"), priceLossColor = c("#FF6E40"),
            symbolColor = c("#99FFE082"), valueColor = c("#CCFFF8E1"),
            dividerColor = c("#22FFE082")
        ),
        // Dark rose with pink highlights
        WidgetColorScheme(
            id = 8, displayName = "Rose Garden",
            bgColor = c("#200A12"), bgDrawableRes = R.drawable.widget_bg_rose_garden,
            titleColor = c("#FCE4EC"), nameColor = c("#FCE4EC"),
            priceGainColor = c("#F48FB1"), priceLossColor = c("#FF5252"),
            symbolColor = c("#99F48FB1"), valueColor = c("#CCFCE4EC"),
            dividerColor = c("#22F48FB1")
        ),
        // iOS-style near-black charcoal
        WidgetColorScheme(
            id = 9, displayName = "Graphite",
            bgColor = c("#1C1C1E"), bgDrawableRes = R.drawable.widget_bg_graphite,
            titleColor = c("#F2F2F7"), nameColor = c("#F2F2F7"),
            priceGainColor = c("#34C759"), priceLossColor = c("#FF3B30"),
            symbolColor = c("#99EBEBF5"), valueColor = c("#CCEBEBF5"),
            dividerColor = c("#22EBEBF5")
        )
    )

    fun getById(id: Int): WidgetColorScheme = all.getOrElse(id) { all[0] }
}
