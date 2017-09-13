package org.altfund.xchangeinterface.xchange.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.knowm.xchange.currency.CurrencyPair;
import java.util.Collection;
import java.util.Date;

@Data
@NoArgsConstructor
public class TradeHistoryParams {

    @JsonProperty("currency_pair")
    private CurrencyPair pair;

    @JsonProperty("currency_pairs")
    private Collection<CurrencyPair> currencyPairs;

    @JsonProperty("end_id")
    private String endId;

    @JsonProperty("end_time")
    private Date endTime;

    @JsonProperty("offset")
    private Long offset;

    @JsonProperty("page_length")
    private Integer pageLength;

    @JsonProperty("page_number")
    private Integer pageNumber;

    @JsonProperty("start_id")
    private String startId;

    @JsonProperty("start_time")
    private Date startTime;
}
