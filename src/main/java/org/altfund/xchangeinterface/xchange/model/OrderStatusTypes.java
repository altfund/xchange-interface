package org.altfund.xchangeinterface.xchange.model;

import lombok.Getter;

@Getter
public enum OrderStatusTypes {

       PLACED(OrderStatusKind.PLACED, "PLACED"),
       GENERAL_EXCHANGE_ERROR(OrderStatusKind.GENERAL_EXCHANGE_ERROR, "GENERAL_EXCHANGE_ERROR"),
       NOT_AVAILABLE_FROM_EXCHANGE(OrderStatusKind.NOT_AVAILABLE_FROM_EXCHANGE, "NOT_AVAILABLE_FROM_EXCHANGE"),
       NOT_YET_IMPLEMENTED_FOR_EXCHANGE(OrderStatusKind.NOT_YET_IMPLEMENTED_FOR_EXCHANGE, "NOT_YET_IMPLEMENTED_FOR_EXCHANGE"),
       NETWORK_ERROR(OrderStatusKind.NETWORK_ERROR, "NETWORK_ERROR"),
       UNKNOWN_ERROR(OrderStatusKind.UNKNOWN_ERROR, "UNKNOWN_ERROR");

       public enum OrderStatusKind {
       PLACED,
       GENERAL_EXCHANGE_ERROR,
       NOT_AVAILABLE_FROM_EXCHANGE,
       NOT_YET_IMPLEMENTED_FOR_EXCHANGE,
       NETWORK_ERROR,
       UNKNOWN_ERROR
       }

    private OrderStatusKind orderStatusKind;
    private String orderStatusPhrase;

    OrderStatusTypes(OrderStatusKind orderStatusKind, String orderStatusPhrase) {
        this.orderStatusKind = orderStatusKind;
        this.orderStatusPhrase = orderStatusPhrase;
    }
}
