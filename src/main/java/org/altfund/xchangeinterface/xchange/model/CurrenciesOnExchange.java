package org.altfund.xchangeinterface.xchange.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class CurrenciesOnExchange {

  @JsonProperty("exchange")
  private String exchange;

  @JsonProperty("currencies")
  private List<String> currencies;

}
