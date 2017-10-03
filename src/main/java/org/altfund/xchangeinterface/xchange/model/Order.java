package org.altfund.xchangeinterface.xchange.model;

import org.altfund.xchangeinterface.xchange.model.ExchangeCredentials;
import org.altfund.xchangeinterface.xchange.model.OrderSpec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Order {

  @JsonProperty("order_type")
  private String orderType;

  @JsonProperty("altfund_id")
  private String altfundId;


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
