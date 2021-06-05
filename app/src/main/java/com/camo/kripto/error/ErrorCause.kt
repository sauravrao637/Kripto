package com.camo.kripto.error

enum class ErrorCause(val message: String) {
    GET_CRPTO_MARKETCAP_DATA("get crypto market cap data"),
    USER_REPORT("user report"),
    UI_ERROR("ui error"),
    GET_MARKET_CHART("chart error"),
    PING_CG("server error"),
    LOAD_IN_ROOM_COINS("load in room coins"),
    LOAD_IN_ROOM_CURRENCIES("load in room currencies"),
    GET_SUPPORTED_CURRENCIES("get supported currencies"),
    GET_GLOBAL_DATA("get global market cap data"),
    GET_GLOBAL_DEFI_DATA("get global defi data"),
    GET_TRENDING("get trending");
}