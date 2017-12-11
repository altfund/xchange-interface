package org.altfund.xchangeinterface.xchange.service.util;

import org.knowm.xchange.currency.Currency;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class CurrencyMixIn {
    CurrencyMixIn(@JsonProperty("currency") Currency currency) { }

    @JsonManagedReference("currency") abstract public Currency getCurrency(); // rename property
}
