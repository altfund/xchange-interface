package org.altfund.xchangeinterface.xchange.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.knowm.xchange.currency.CurrencyPair;
import java.util.Collection;

@Data
@NoArgsConstructor
public class OpenOrderParams {

    @JsonProperty("base_currency")
    private String baseCurrency;

    @JsonProperty("quote_currency")
    private String quoteCurrency;

    @JsonProperty("currency_pair")
    private CurrencyPair currencyPair;

    @JsonProperty("currency_pairs")
    private Collection<CurrencyPair> currencyPairs;
}
