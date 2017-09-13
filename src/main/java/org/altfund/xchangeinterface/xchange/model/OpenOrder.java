package org.altfund.xchangeinterface.xchange.model;

import org.altfund.xchangeinterface.xchange.model.ExchangeCredentials;
import org.altfund.xchangeinterface.xchange.model.OpenOrderParams;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
public class OpenOrder {

  @JsonProperty("open_order_params")
  private OpenOrderParams openOrderParams;

  @JsonProperty("exchange_credentials")
  private ExchangeCredentials exchangeCredentials;

  public OpenOrder(OpenOrderParams openOrderParams, ExchangeCredentials exchangeCredentials) {
    this.openOrderParams = openOrderParams;
    this.exchangeCredentials = exchangeCredentials;
  }
}
