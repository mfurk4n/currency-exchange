package com.finexchange.finexchange.enums;

public enum ExchangeCode {
    USD_TRY("USD", "TRY"),
    EUR_TRY("EUR", "TRY"),
    GBP_TRY("GBP", "TRY"),
    CHF_TRY("CHF", "TRY"),
    CAD_TRY("CAD", "TRY"),
    AUD_TRY("AUD", "TRY"),
    CNY_TRY("CNY", "TRY"),
    JPY_TRY("JPY", "TRY"),
    RUB_TRY("RUB", "TRY"),
    SAR_TRY("SAR", "TRY"),
    DKK_TRY("DKK", "TRY"),
    SEK_TRY("SEK", "TRY"),
    NOK_TRY("NOK", "TRY"),
    BGN_TRY("BGN", "TRY"),
    RON_TRY("RON", "TRY"),

    //CAPRAZLAR
    EUR_USD("EUR", "USD"),
    GBP_USD("GBP", "USD"),
    USD_CHF("USD", "CHF"),
    USD_CAD("USD", "CAD"),
    USD_AUD("USD", "AUD"),
    USD_CNY("USD", "CNY"),
    USD_JPY("USD", "JPY"),
    USD_RUB("USD", "RUB"),
    USD_SAR("USD", "SAR"),
    USD_DKK("USD", "DKK"),
    USD_SEK("USD", "SEK"),
    USD_NOK("USD", "NOK"),
    USD_BGN("USD", "BGN"),
    USD_RON("USD", "RON"),
    USD_KRW("USD", "KRW");


    private final String baseCurrency;
    private final String targetCurrency;

    ExchangeCode(String baseCurrency, String targetCurrency) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    @Override
    public String toString() {
        return baseCurrency + "/" + targetCurrency;
    }
}
