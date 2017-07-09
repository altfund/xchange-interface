package org.altfund.xchangeinterface.restApi.currency;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;

public class CurrencyMap {

    //private final XChangeService xChangeService;
    private Map<String, String> currencies;

    //CurrencyMap isn't called like objectProcessor, and it won't compile beacuse I don't
    //thing currencycontroller can give it anything. we need to create a new
    //class that gets the xchangeservice as called by something in something like
    //a QueueConfig. If that also had an object mapper. Then maybe CurrencyMap
    //could call that function? is there any way this could be currencycontroller?

    public CurrencyMap(Map<String, String> currencies) {
        this.currencies = currencies;
    }

    @JsonAnyGetter
    public Map<String, String> getCurrencies() {
        return currencies;
    }
}

