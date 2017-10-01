package org.altfund.xchangeinterface.xchange.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class MarketByExchanges {

  @JsonProperty("base_currency")
  private String baseCurrency;

  @JsonProperty("quote_currency")
  private String quoteCurrency;

  @JsonProperty("exchanges")
  private List<String> exchanges;

}
