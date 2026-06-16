package com.example.stocktracker.data.remote

import com.google.gson.annotations.SerializedName

data class YahooChartResponse(
    @SerializedName("chart") val chart: ChartWrapper?
) {
    data class ChartWrapper(
        @SerializedName("result") val result: List<ChartResult>?
    )
    data class ChartResult(
        @SerializedName("meta") val meta: Meta?
    )
    data class Meta(
        @SerializedName("regularMarketPrice") val regularMarketPrice: Double?,
        @SerializedName("regularMarketChangePercent") val regularMarketChangePercent: Double?,
        @SerializedName("currency") val currency: String?
    )
}
