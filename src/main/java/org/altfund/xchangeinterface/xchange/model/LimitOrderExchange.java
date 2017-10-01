package org.altfund.xchangeinterface.xchange.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.knowm.xchange.dto.trade.LimitOrder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LimitOrderExchange  {

    @JsonProperty("exchange")
    private String exchange;

    @JsonProperty("limit_order")
    private LimitOrder limitOrder;
}
