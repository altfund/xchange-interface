package org.altfund.xchangeinterface.xchange.service.util;

import org.knowm.xchange.currency.Currency;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class FundingRecordMixIn {
    //MixIn(@JsonProperty("width") int w, @JsonProperty("height") int h) { }
    //FundingRecordMixIn(@JsonManagedReference("currency") Currency currency) { }
    FundingRecordMixIn(@JsonProperty("currency") Currency currency) { }

    @JsonBackReference("currency") abstract public Currency getCurrency(); // rename property

    /*
    @JsonProperty("width") abstract int getW(); // rename property
    @JsonProperty("height") abstract int getH(); // rename property
    @JsonIgnore int getSize(); // we don't need it!
    */
}
