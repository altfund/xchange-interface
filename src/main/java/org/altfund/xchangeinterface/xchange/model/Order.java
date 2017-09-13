package org.altfund.xchangeinterface.xchange.model;

import org.altfund.xchangeinterface.xchange.model.ExchangeCredentials;
import org.altfund.xchangeinterface.xchange.model.OrderSpec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * Created by lcsontos on 6/27/17.
 */
@Data
@NoArgsConstructor
public class Order {

  @JsonProperty("order_type")
  private String orderType;

  @JsonProperty("order_specs")
  private OrderSpec orderSpec;

  @JsonProperty("order_id")
  private String orderId;

  @JsonProperty("exchange_credentials")
  private ExchangeCredentials exchangeCredentials;

  public Order(OrderSpec orderSpec, ExchangeCredentials exchangeCredentials) {
    this.orderSpec = orderSpec;
    this.exchangeCredentials = exchangeCredentials;
  }
}
