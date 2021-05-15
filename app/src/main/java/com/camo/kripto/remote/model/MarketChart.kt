package com.camo.kripto.remote.model

data class MarketChart(
    val prices: List<List<Double>>,
    val market_caps: List<List<Double>>,
    val total_volumes: List<List<Double>>
)