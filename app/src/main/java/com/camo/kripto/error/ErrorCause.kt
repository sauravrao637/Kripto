package com.camo.kripto.error

import java.lang.Exception

enum class ErrorCause(val message: String) {
    GET_CRYPTO_MARKETA_DATA("get crypto market cap data"),
    USER_REPORT("user report"),
    UI_ERROR("ui error"),
    GET_MARKET_CHART("chart error"),
    PING_CG("server error"),
    LOAD_IN_ROOM_COINS("load in room coins"),
    LOAD_IN_ROOM_CURRENCIES("load in room currencies"),
    GET_SUPPORTED_CURRENCIES("get supported currencies"),
    GET_GLOBAL_DATA("get global market cap data"),
    GET_GLOBAL_DEFI_DATA("get global defi data"),
    GET_TRENDING("get trending"),
    SYNC_FAILED("sync failed"),
    EXCHANGE_RATE("couldn't get exchange rates"),
    GENERAL_ERROR("testing error"),
    ROOM_ERROR("room database error"),
    GET_SIMPLE_PRICE("get simple price error"),
    EMPTY_COIN_ID_NAME("null coin id or/and name");
}
class InvalidResponse: Exception()