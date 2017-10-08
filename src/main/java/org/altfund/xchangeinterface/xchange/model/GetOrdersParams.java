package org.altfund.xchangeinterface.xchange.model;

import org.altfund.xchangeinterface.xchange.model.ExchangeCredentials;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.knowm.xchange.currency.CurrencyPair;
import java.util.Collection;
import java.util.Date;

@Data
@NoArgsConstructor
public class GetOrdersParams {

    @JsonProperty("exchange_credentials")
    private ExchangeCredentials exchangeCredentials;

    @JsonProperty("order_ids")
    private String[] orderIds;
}
