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

  private Order order;

  private String buyOrderId;
  private OrderStatus buyOrderStatus;
  private LocalDateTime buyTimestamp;

  private String sellOrderId;
  private OrderStatus sellOrderStatus;
  private LocalDateTime sellTimestamp;

  public boolean isRetryable() {
     // log.debug("buy order status error + ", buyOrderStatus.getOrderStatusKind());
      //log.debug("sell order status error + ", sellOrderStatus.getOrderStatusKind());
    if (buyOrderStatus.hasStatus(NETWORK_ERROR)) {
      return true;
    }

    if (sellOrderStatus.hasStatus(NETWORK_ERROR)) {
      return true;
    }

    return false;
  }
}
