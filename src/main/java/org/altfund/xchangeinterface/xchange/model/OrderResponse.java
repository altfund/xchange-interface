package org.altfund.xchangeinterface.xchange.model;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.NETWORK_ERROR;

@Builder
@Getter
@Slf4j
public class OrderResponse {

  private final String orderResponseId = UUID.randomUUID().toString();

  private OrderSpec orderSpec;
  private String orderType;

  // private String buyOrderId;
  //private OrderStatus buyOrderStatus;
  //private LocalDateTime buyTimestamp;

  private String orderId;
  private String altfundId;
  private OrderStatus orderStatus;
  private LocalDateTime timestamp;
  private String api = "knowm";

  /*
   * TODO put back, this is useful
  public boolean isRetryable() {
     // log.debug("buy order status error + ", buyOrderStatus.getOrderStatusKind());
      //log.debug("sell order status error + ", sellOrderStatus.getOrderStatusKind());
    if (getBuyOrderStatus().hasStatus(NETWORK_ERROR)) {
      return true;
    }

    if (getSellOrderStatus().hasStatus(NETWORK_ERROR)) {
      return true;
    }

    return false;
  }
  */
}
