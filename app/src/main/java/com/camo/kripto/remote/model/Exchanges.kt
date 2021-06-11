package com.camo.kripto.remote.model

class Exchanges : ArrayList<Exchanges.ExchangesItem>(){
    data class ExchangesItem(
        val id: String,
        val name: String,
        val year_established: Int,
        val country: String?,
        val description: String?,
        val url: String,
        val image: String,
        val has_trading_incentive: Boolean,
        val trust_score: Int,
        val trust_score_rank: Int,
        val trade_volume_24h_btc: Double,
        val trade_volume_24h_btc_normalized: Double
    )
}