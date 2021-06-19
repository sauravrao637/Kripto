package com.camo.kripto.remote.model

import java.math.BigDecimal

data class MarketChart(
    val prices: List<List<BigDecimal>>,
    val market_caps: List<List<BigDecimal>>,
    val total_volumes: List<List<BigDecimal>>
)