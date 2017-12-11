package org.altfund.xchangeinterface.xchange.service.util;

import org.knowm.xchange.currency.Currency;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class CurrencyMixIn {
    CurrencyMixIn(@JsonProperty("currency") Currency currency) { }

    @JsonBackReference("currency") abstract public Currency getIso4217Currency();
    @JsonBackReference("currency") abstract public Currency getCommonlyUsedCurrency();
}
