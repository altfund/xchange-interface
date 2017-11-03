package org.altfund.xchangeinterface.xchange.model;

import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.Optional;
import java.io.StringWriter;
import java.io.PrintWriter;

import org.altfund.xchangeinterface.xchange.model.OrderStatusTypes;

//private enum OrderStatus {
//
//  PLACED(OrderStatusKind.SUCCESS, ""),
//  GENERAL_EXCHANGE_ERROR(OrderStatusKind.EXCHANGE_ERROR, "Exchange reported an error with the request"),
//  NOT_AVAILABLE_FROM_EXCHANGE(OrderStatusKind.EXCHANGE_ERROR, "Exchange doesn't support the requested operation"),
//  NOT_YET_IMPLEMENTED_FOR_EXCHANGE(OrderStatusKind.EXCHANGE_ERROR, "Exchange supports the requested operation, but it hasn't yet been implemented."),
//  NETWORK_ERROR(OrderStatusKind.NETWORK_ERROR, "Networking error occurred"),
//  UNKNOWN_ERROR(OrderStatusKind.UNKNOWN_ERROR, "Unknown error");
//
//  public enum OrderStatusKind {
//    SUCCESS,
//    EXCHANGE_ERROR,
//    NETWORK_ERROR,
//    UNKNOWN_ERROR
//  }
//}

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class OrderStatus {
    public OrderStatusTypes orderStatusType;
    public String orderStatusPhrase;
    int maxLength;

    /*
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
       */

    public OrderStatus(OrderStatusTypes orderStatusType, String orderStatusPhrase) {
        this.orderStatusType = orderStatusType;
        this.orderStatusPhrase = orderStatusPhrase;
    }

    /*
       public OrderStatus(Exception e) {
       this.orderStatusKind = e.getClass().getCanonicalName();
       this.orderStatusPhrase = e.getMessage();
       }

       public OrderStatus(String error, Exception e) {
       this.orderStatusKind = error;
       this.orderStatusPhrase = e.getMessage() + " :: " + e.getClass().getCanonicalName();
       maxLength = (this.orderStatusPhrase.length() < 199)?this.orderStatusPhrase.length():199;
       this.orderStatusPhrase = this.orderStatusPhrase.substring(0, maxLength);
       }
       */

    public OrderStatus(OrderStatusTypes orderStatusType, Exception e) {
        this.orderStatusType = orderStatusType;
        this.orderStatusPhrase = e.getClass().getCanonicalName() + " :: " + getCurrentStackTraceString(e);
        //maxLength = (this.orderStatusPhrase.length() < 199)?this.orderStatusPhrase.length():199;
        //this.orderStatusPhrase = this.orderStatusPhrase.substring(0, maxLength);
    }

    private String getCurrentStackTraceString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public boolean hasStatus(OrderStatusTypes code) {

        Optional<OrderStatusTypes> orderStatus =
            Optional.ofNullable(getOrderStatusType());

        if (orderStatus.isPresent()) {
            return code == orderStatus.get();
        }
        else {
            return false;
        }
    }

    public OrderStatusTypes getOrderStatusType() {
        return this.orderStatusType;
    }
    public void setOrderStatusType(OrderStatusTypes orderStatusType) {
        this.orderStatusType = orderStatusType;
    }
    public String getOrderStatusPhrase() {
        return this.orderStatusPhrase;
    }
    public void setOrderStatusPhrase(String phrase) {
        this.orderStatusPhrase = phrase;
    }
}
