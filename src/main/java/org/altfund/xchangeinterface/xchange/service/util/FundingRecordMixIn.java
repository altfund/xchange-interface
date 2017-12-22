package org.altfund.xchangeinterface.xchange.service.util;

import org.knowm.xchange.currency.Currency;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class FundingRecordMixIn {
    FundingRecordMixIn(@JsonProperty("currency") Currency currency) { }

    @JsonManagedReference("currency") abstract public Currency getCurrency();
}
