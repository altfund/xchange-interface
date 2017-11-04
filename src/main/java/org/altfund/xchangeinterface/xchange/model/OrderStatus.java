package org.altfund.xchangeinterface.xchange.model;

import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.Optional;
import java.io.StringWriter;
import java.io.PrintWriter;

import org.altfund.xchangeinterface.xchange.model.OrderStatusTypes;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class OrderStatus {
    public OrderStatusTypes orderStatusType;
    public String orderStatusPhrase;
    int maxLength;

    public OrderStatus(OrderStatusTypes orderStatusType, String orderStatusPhrase) {
        this.orderStatusType = orderStatusType;
        this.orderStatusPhrase = orderStatusPhrase;
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
