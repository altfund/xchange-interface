package org.altfund.xchangeinterface.xchange.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum OrderStatusTypes {

       PLACED(OrderStatusKind.PLACED, "PLACED"),
       GENERAL_EXCHANGE_ERROR(OrderStatusKind.GENERAL_EXCHANGE_ERROR, "GENERAL_EXCHANGE_ERROR"),
       NOT_AVAILABLE_FROM_EXCHANGE(OrderStatusKind.NOT_AVAILABLE_FROM_EXCHANGE, "NOT_AVAILABLE_FROM_EXCHANGE"),
       NOT_YET_IMPLEMENTED_FOR_EXCHANGE(OrderStatusKind.NOT_YET_IMPLEMENTED_FOR_EXCHANGE, "NOT_YET_IMPLEMENTED_FOR_EXCHANGE"),
       NETWORK_ERROR(OrderStatusKind.NETWORK_ERROR, "NETWORK_ERROR"),
       INSUFFICIENT_FUNDS(OrderStatusKind.INSUFFICIENT_FUNDS, "INSUFFICIENT_FUNDS"),
       INVALID_ARGUMENTS_VOLUME(OrderStatusKind.INVALID_ARGUMENTS_VOLUME, "INVALID_ARGUMENTS_VOLUME"),
       ORDER_TOO_SMALL(OrderStatusKind.ORDER_TOO_SMALL, "ORDER_TOO_SMALL"),
       ORDER_TOO_LARGE(OrderStatusKind.ORDER_TOO_LARGE, "ORDER_TOO_LARGE"),
       PRICE_TOO_SMALL(OrderStatusKind.PRICE_TOO_SMALL, "PRICE_TOO_SMALL"),
       PRICE_TOO_LARGE(OrderStatusKind.PRICE_TOO_LARGE, "PRICE_TOO_LARGE"),
       UNKNOWN_ERROR(OrderStatusKind.UNKNOWN_ERROR, "UNKNOWN_ERROR"),
       CANCELED(OrderStatusKind.CANCELED, "CANCELED"),
       CANCEL_FAILED(OrderStatusKind.CANCEL_FAILED, "CANCEL_FAILED"),
       PROCESSING_FAILED(OrderStatusKind.PROCESSING_FAILED, "PROCESSING_FAILED");

       public enum OrderStatusKind {
           PLACED,
           GENERAL_EXCHANGE_ERROR,
           NOT_AVAILABLE_FROM_EXCHANGE,
           NOT_YET_IMPLEMENTED_FOR_EXCHANGE,
           NETWORK_ERROR,
           INSUFFICIENT_FUNDS,
           PRICE_TOO_SMALL,
           PRICE_TOO_LARGE,
           ORDER_TOO_SMALL,
           ORDER_TOO_LARGE,
           INVALID_ARGUMENTS_VOLUME,
           UNKNOWN_ERROR,
           CANCELED,
           CANCEL_FAILED,
           PROCESSING_FAILED
       }

    private OrderStatusKind orderStatusKind;
    private String orderStatusPhrase;

    OrderStatusTypes(OrderStatusKind orderStatusKind, String orderStatusPhrase) {
        this.orderStatusKind = orderStatusKind;
        this.orderStatusPhrase = orderStatusPhrase;
    }
}
