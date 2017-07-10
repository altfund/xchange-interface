package org.altfund.xchangeinterface.restApi.currency;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;

public class CurrencyMap {

    private Map<String, String> currencies;

    public CurrencyMap(Map<String, String> currencies) {
        this.currencies = currencies;
    }

    @JsonAnyGetter
    public Map<String, String> getCurrencies() {
        return currencies;
    }
}

