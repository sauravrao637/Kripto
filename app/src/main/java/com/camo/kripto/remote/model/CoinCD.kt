package com.camo.kripto.remote.model

import java.math.BigDecimal

data class CoinCD(
    val id: String,
    val symbol: String,
    val name: String,
//    val asset_platform_id: Any,
    val block_time_in_minutes: Int,
    val hashing_algorithm: String,
    val categories: List<String>,
//    val public_notice: Any,
//    val additional_notices: List<Any>,
    val description: Map<String, String>,
    val links: Links,
    val image: Image,
    val country_origin: String,
    val genesis_date: String,
    val sentiment_votes_up_percentage: BigDecimal,
    val sentiment_votes_down_percentage: BigDecimal,
    val market_cap_rank: Int,
    val coingecko_rank: Int,
    val coingecko_score: BigDecimal,
    val developer_score: BigDecimal,
    val community_score: BigDecimal,
    val liquidity_score: BigDecimal,
    val public_interest_score: BigDecimal,
    val market_data: MarketData,
    val community_data: CommunityData,
    val public_interest_stats: PublicInterestStats,
//    val status_updates: List<Any>,
    val last_updated: String
) {


    data class Links(
        val homepage: List<String?>?,
        val blockchain_site: List<String?>?,
        val official_forum_url: List<String?>?,
        val chat_url: List<String?>?,
        val announcement_url: List<String?>?,
        val twitter_screen_name: String?,
        val facebook_username: String?,
        val bitcointalk_thread_identifier: Any,
        val telegram_channel_identifier: String?,
        val subreddit_url: String?,
        val repos_url: Map<String,List<String>>
    )


    data class Image(
        val thumb: String,
        val small: String,
        val large: String
    )

    data class MarketData(
        val current_price: Map<String, BigDecimal>,
//        val total_value_locked: Any,
//        val mcap_to_tvl_ratio: Any,
//        val fdv_to_tvl_ratio: Any,
//        val roi: Any,
        val ath: Map<String, BigDecimal>,
        val ath_change_percentage: Map<String, BigDecimal>,
        val ath_date: Map<String, String>,
        val atl: Map<String, BigDecimal>,
        val atl_change_percentage: Map<String, BigDecimal>,
        val atl_date: Map<String, String>,
        val market_cap: Map<String, BigDecimal>,
        val market_cap_rank: Int,
        val fully_diluted_valuation: Map<String, BigDecimal>,
        val total_volume: Map<String, BigDecimal>,
        val high_24h: Map<String, BigDecimal>,
        val low_24h: Map<String, BigDecimal>,
//        val price_change_24h: BigDecimal,
//        val price_change_percentage_24h: BigDecimal,
//        val price_change_percentage_7d: BigDecimal,
//        val price_change_percentage_14d: BigDecimal,
//        val price_change_percentage_30d: BigDecimal,
//        val price_change_percentage_60d: BigDecimal,
//        val price_change_percentage_200d: BigDecimal,
//        val price_change_percentage_1y: BigDecimal,
//        val market_cap_change_24h: BigDecimal,
//        val market_cap_change_percentage_24h: BigDecimal,
        val price_change_24h_in_currency: Map<String, BigDecimal>,
        val price_change_percentage_1h_in_currency: Map<String, BigDecimal>,
        val price_change_percentage_24h_in_currency: Map<String, BigDecimal>,
        val price_change_percentage_7d_in_currency: Map<String, BigDecimal>,
        val price_change_percentage_14d_in_currency: Map<String, BigDecimal>,
        val price_change_percentage_30d_in_currency: Map<String, BigDecimal>,
        val price_change_percentage_60d_in_currency: Map<String, BigDecimal>,
        val price_change_percentage_200d_in_currency: Map<String, BigDecimal>,
        val price_change_percentage_1y_in_currency: Map<String, BigDecimal>,
        val market_cap_change_24h_in_currency: Map<String, BigDecimal>,
        val market_cap_change_percentage_24h_in_currency: Map<String, BigDecimal>,
        val total_supply: BigDecimal,
        val max_supply: BigDecimal,
        val circulating_supply: BigDecimal,
        val last_updated: String
    )

    data class CommunityData(
        val facebook_likes: Long,
        val twitter_followers: Long,
        val reddit_average_posts_48h: BigDecimal,
        val reddit_average_comments_48h: BigDecimal,
        val reddit_subscribers: Long,
        val reddit_accounts_active_48h: Long,
        val telegram_channel_user_count: Long
    )

    data class PublicInterestStats(
        val alexa_rank: Int,
        val bing_matches: Long
    )
}