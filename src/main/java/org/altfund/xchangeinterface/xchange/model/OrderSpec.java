package org.altfund.xchangeinterface.xchange.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSpec {

  @JsonProperty("base_currency")
  private Currency baseCurrency;

  @JsonProperty("quote_currency")
  private Currency quoteCurrency;

  @JsonProperty("volume")
  private BigDecimal volume;

  @JsonProperty("price")
  private BigDecimal price;

  @JsonProperty("test")
  private boolean test;
}
