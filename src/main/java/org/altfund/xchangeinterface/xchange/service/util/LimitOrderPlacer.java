package org.altfund.xchangeinterface.xchange.service.util;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;
import java.math.BigDecimal;
import static java.time.ZoneOffset.UTC;

import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.trade.LimitOrder;
import static org.knowm.xchange.dto.Order.OrderType.ASK;
import static org.knowm.xchange.dto.Order.OrderType.BID;

import org.altfund.xchangeinterface.util.JsonHelper;
import org.altfund.xchangeinterface.xchange.model.Order;
import org.altfund.xchangeinterface.xchange.model.OrderSpec;
import org.altfund.xchangeinterface.xchange.model.OrderStatus;
import org.altfund.xchangeinterface.xchange.model.OrderResponse;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.PLACED;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.GENERAL_EXCHANGE_ERROR;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.NOT_AVAILABLE_FROM_EXCHANGE;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.NOT_YET_IMPLEMENTED_FOR_EXCHANGE;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.NETWORK_ERROR;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.UNKNOWN_ERROR;

import java.io.IOException;
import org.altfund.xchangeinterface.xchange.service.util.JsonifyExceptions;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

@Slf4j
public class LimitOrderPlacer {

    public OrderResponse placeOrder(
            Order order,
            TradeService tradeService,
            CurrencyPair currencyPair,
            int scale,
            JsonHelper jh) throws XChangeServiceException {

        LimitOrder lo = null;
        OrderResponse orderResponse = null;
        OrderResponse.OrderResponseBuilder orderResponseBuilder = OrderResponse
        .builder()
        .order(order);

        if ("ASK".equals(order.getOrderType())) {
            lo = new LimitOrder.Builder(ASK, currencyPair)
                .tradableAmount(
                        order.getOrderSpec().getVolume().setScale(scale, BigDecimal.ROUND_HALF_EVEN)
                        )
                .limitPrice(order.getOrderSpec().getPrice())
                .build();
        }
        else {
            lo = new LimitOrder.Builder(BID, currencyPair)
                .tradableAmount(
                        order.getOrderSpec().getVolume().setScale(scale, BigDecimal.ROUND_HALF_EVEN)
                        )
                .limitPrice(order.getOrderSpec().getPrice())
                .build();
        }

        OrderStatus sellOrderStatus = null;
        OrderStatus buyOrderStatus = null;

        if (order.getOrderSpec().isTest()) {
            //TODO get orderid?

            if ("ASK".equals(order.getOrderType())) {
                verifyOrder(
                    tradeService,
                    lo,
                    orderResponseBuilder::sellOrderStatus,
                    orderResponseBuilder::sellTimestamp
                    );
            }
            else {
                verifyOrder(
                    tradeService,
                    lo,
                    orderResponseBuilder::buyOrderStatus,
                    orderResponseBuilder::buyTimestamp
                    );
            }
        }
        else  {

            if ("ASK".equals(order.getOrderType())) {
                sellOrderStatus = placeOrder(
                    tradeService,
                    lo,
                    orderResponseBuilder::sellOrderId,
                    orderResponseBuilder::sellOrderStatus,
                    orderResponseBuilder::sellTimestamp
                    );
            }
            else {
                buyOrderStatus = placeOrder(
                    tradeService,
                    lo,
                    orderResponseBuilder::buyOrderId,
                    orderResponseBuilder::buyOrderStatus,
                    orderResponseBuilder::buyTimestamp
                    );
            }
        }
        return orderResponseBuilder.build();
    }

    private OrderStatus placeOrder(
            TradeService tradeService, LimitOrder limitOrder,
            Consumer<String> orderIdConsumer, Consumer<OrderStatus> orderStatusConsumer,
            Consumer<LocalDateTime> orderTimestampConsumer) {

        OrderStatus orderStatus;
        try {
            String limitOrderId = tradeService.placeLimitOrder(limitOrder);
            orderIdConsumer.accept(limitOrderId);
            orderStatus = new OrderStatus(PLACED, "");
            log.debug("order placed.");
        }
        catch (Exception e) {
            log.error("Placing order " + limitOrder + " has failed.", e);
            log.error("failed to place order in prod:\n type: {}, \nreason: {}. \n, \ntrace: {}.",
                    e.getClass().getCanonicalName(),
                    e.getMessage(),
                    e.getStackTrace());
            orderStatus = translateException(e);
        }

        orderStatusConsumer.accept(orderStatus);
        orderTimestampConsumer.accept(LocalDateTime.now(UTC));

        return orderStatus;
            }

    private OrderStatus verifyOrder(
            TradeService tradeService, LimitOrder limitOrder,
            Consumer<OrderStatus> orderStatusConsumer, Consumer<LocalDateTime> orderTimestampConsumer) {

        OrderStatus orderStatus;
        try {
            tradeService.verifyOrder(limitOrder);
            orderStatus = new OrderStatus(PLACED, "");
            //log.debug("order placed {}.", orderStatus.getOrderStatusKind());
        } catch (Exception e) {
            log.error("failed to verify order in test:\n type: {}, \nreason: {}. \n.",
                    e.getClass().getCanonicalName(),
                    e.getMessage());
            orderStatus = translateException(e);
        }

        orderStatusConsumer.accept(orderStatus);
        orderTimestampConsumer.accept(LocalDateTime.now(UTC));

        return orderStatus;
            }

    //private OrderStatus orderStatusPlaced() {
    //   return new OrderStatus("PLACED", "");
    //}

    //TODO make all reponses use order status via translate exception
    //use a jsonify exception class maybe?
    private OrderStatus translateException(Exception e) {
        if (e instanceof IOException) {
            e.printStackTrace();
            return new OrderStatus(NETWORK_ERROR, e);
            //return OrderStatus.NETWORK_ERROR;
        } else if (e instanceof ExchangeException) {
            //e.printStackTrace();
            //return OrderStatus.GENERAL_EXCHANGE_ERROR;
            return new OrderStatus(GENERAL_EXCHANGE_ERROR, e);
        } else if (e instanceof IllegalArgumentException) {
            //e.printStackTrace();
            //return OrderStatus.GENERAL_EXCHANGE_ERROR;
            return new OrderStatus(GENERAL_EXCHANGE_ERROR, e);
        } else if (e instanceof NotAvailableFromExchangeException) {
            e.printStackTrace();
            //return OrderStatus.NOT_AVAILABLE_FROM_EXCHANGE;
            return new OrderStatus(NOT_AVAILABLE_FROM_EXCHANGE, e);
        } else if (e instanceof NotYetImplementedForExchangeException) {
            e.printStackTrace();
            //return OrderStatus.NOT_YET_IMPLEMENTED_FOR_EXCHANGE;
            return new OrderStatus(NOT_YET_IMPLEMENTED_FOR_EXCHANGE, e);
        } else {
            return new OrderStatus(UNKNOWN_ERROR, e);
            //e.printStackTrace();
            //return OrderStatus.UNKNOWN_ERROR;
            //return new OrderStatus(UNKNOWN_ERROR, e);
        }
    }
}
