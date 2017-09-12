package org.altfund.xchangeinterface.xchange.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by lcsontos on 6/27/17.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSpec {

  @JsonProperty("buy_base_currency")
  private Currency baseCurrency;

  @JsonProperty("buy_quote_currency")
  private Currency quoteCurrency;

  @JsonProperty("buy_volume")
  private BigDecimal volume;

  @JsonProperty("buy_price")
  private BigDecimal price;

  @JsonProperty("buy_exchange")
  private Exchange exchange;

  @JsonProperty("test")
  private boolean test;
}
