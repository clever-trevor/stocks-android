package com.example.stocktracker.data.remote

import com.google.gson.annotations.SerializedName

data class ForexResponse(
    @SerializedName("Realtime Currency Exchange Rate") val exchangeRate: ExchangeRate?
)

data class ExchangeRate(
    @SerializedName("5. Exchange Rate") val rate: String?
)
