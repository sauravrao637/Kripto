package com.camo.kripto.data.model

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
    val sentiment_votes_up_percentage: Double,
    val sentiment_votes_down_percentage: Double,
    val market_cap_rank: Int,
    val coingecko_rank: Int,
    val coingecko_score: Double,
    val developer_score: Double,
    val community_score: Double,
    val liquidity_score: Double,
    val public_interest_score: Double,
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
        val current_price: Map<String, Double>,
//        val total_value_locked: Any,
//        val mcap_to_tvl_ratio: Any,
//        val fdv_to_tvl_ratio: Any,
//        val roi: Any,
        val ath: Map<String, Double>,
        val ath_change_percentage: Map<String, Double>,
        val ath_date: Map<String, String>,
        val atl: Map<String, Double>,
        val atl_change_percentage: Map<String, Double>,
        val atl_date: Map<String, String>,
        val market_cap: Map<String, Double>,
        val market_cap_rank: Int,
        val fully_diluted_valuation: Map<String, Double>,
        val total_volume: Map<String, Double>,
        val high_24h: Map<String, Double>,
        val low_24h: Map<String, Double>,
//        val price_change_24h: Double,
//        val price_change_percentage_24h: Double,
//        val price_change_percentage_7d: Double,
//        val price_change_percentage_14d: Double,
//        val price_change_percentage_30d: Double,
//        val price_change_percentage_60d: Double,
//        val price_change_percentage_200d: Double,
//        val price_change_percentage_1y: Double,
//        val market_cap_change_24h: Double,
//        val market_cap_change_percentage_24h: Double,
        val price_change_24h_in_currency: Map<String, Double>,
        val price_change_percentage_1h_in_currency: Map<String, Double>,
        val price_change_percentage_24h_in_currency: Map<String, Double>,
        val price_change_percentage_7d_in_currency: Map<String, Double>,
        val price_change_percentage_14d_in_currency: Map<String, Double>,
        val price_change_percentage_30d_in_currency: Map<String, Double>,
        val price_change_percentage_60d_in_currency: Map<String, Double>,
        val price_change_percentage_200d_in_currency: Map<String, Double>,
        val price_change_percentage_1y_in_currency: Map<String, Double>,
        val market_cap_change_24h_in_currency: Map<String, Double>,
        val market_cap_change_percentage_24h_in_currency: Map<String, Double>,
        val total_supply: Double,
        val max_supply: Double,
        val circulating_supply: Double,
        val last_updated: String
    )

    data class CommunityData(
        val facebook_likes: Long,
        val twitter_followers: Long,
        val reddit_average_posts_48h: Double,
        val reddit_average_comments_48h: Double,
        val reddit_subscribers: Long,
        val reddit_accounts_active_48h: Long,
        val telegram_channel_user_count: Long
    )

    data class PublicInterestStats(
        val alexa_rank: Int,
        val bing_matches: Long
    )
}