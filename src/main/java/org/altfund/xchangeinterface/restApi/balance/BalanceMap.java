package org.altfund.xchangeinterface.restApi.balance;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;

public class BalanceMap {
    private Map<String, Map<String, String>> balances;

    public BalanceMap(Map<String, Map<String, String>> balances) {
        this.balances = balances;
    }

    @JsonAnyGetter
    public Map<String, Map<String, String>> getBalances() {
        return balances;
    }
}

