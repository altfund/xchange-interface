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

import org.altfund.xchangeinterface.xchange.service.util.ExchangeScale;
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
import org.altfund.xchangeinterface.xchange.service.util.ExtractExceptions;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

@Slf4j
public class LimitOrderPlacer {
    private final ExchangeScale exchangeScale;

    public LimitOrderPlacer(ExchangeScale exchangeScale) {
        this.exchangeScale = exchangeScale;
    }

    public OrderResponse placeOrder(
            Order order,
            TradeService tradeService,
            CurrencyPair currencyPair,
            org.knowm.xchange.Exchange exchange,
            JsonHelper jh) throws XChangeServiceException {

        LimitOrder lo = null;
        OrderResponse orderResponse = null;
        OrderResponse.OrderResponseBuilder orderResponseBuilder = OrderResponse
            .builder()
            .orderType(order.getOrderType())
            .altfundId(order.getAltfundId())
            .orderSpec(order.getOrderSpec());

        //TODO need to honor CurrencyPairMetaData.getPriceScale() BEFORE individual
        //currency scale.

        log.debug("currency pair is {}", currencyPair);

        int baseScale = exchangeScale.getBaseScale(currencyPair, exchange);
        int quoteScale = exchangeScale.getQuoteScale(currencyPair, exchange);

        BigDecimal base = order.getOrderSpec().getVolume();
        BigDecimal quote = order.getOrderSpec().getPrice();

        base = base.setScale(baseScale, BigDecimal.ROUND_HALF_EVEN);
        quote = quote.setScale(quoteScale, BigDecimal.ROUND_HALF_EVEN);

        log.debug("Submitting limit order with following base amount {} at scale {} and quote price {} at scale {}", base, baseScale, quote, quoteScale);

        if ("ASK".equals(order.getOrderType())) {
            lo = new LimitOrder.Builder(ASK, currencyPair)
                .originalAmount(base)
                .limitPrice(quote)
                //.id(order.getOrderId())
                .build();
        }
        else {
            lo = new LimitOrder.Builder(BID, currencyPair)
                .originalAmount(base)
                .limitPrice(quote)
                //.id(order.getOrderId())
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
                        orderResponseBuilder::orderStatus,
                        orderResponseBuilder::timestamp
                        );
            }
            else {
                verifyOrder(
                        tradeService,
                        lo,
                        orderResponseBuilder::orderStatus,
                        orderResponseBuilder::timestamp
                        );
            }
        }
        else  {

            if ("ASK".equals(order.getOrderType())) {
                sellOrderStatus = placeOrder(
                        tradeService,
                        lo,
                        orderResponseBuilder::orderId,
                        orderResponseBuilder::orderStatus,
                        orderResponseBuilder::timestamp
                        );
            }
            else {
                buyOrderStatus = placeOrder(
                        tradeService,
                        lo,
                        orderResponseBuilder::orderId,
                        orderResponseBuilder::orderStatus,
                        orderResponseBuilder::timestamp
                        );
            }
        }
        try {
            log.debug("built order response {}", jh.getObjectMapper().writeValueAsString(orderResponseBuilder.build()));
        }
        catch(Exception ex) {
            log.error("json processing error {}", ex.getMessage());
        }
        return orderResponseBuilder.build();
            }

    private OrderStatus placeOrder(
            TradeService tradeService, LimitOrder limitOrder,
            Consumer<String> orderIdConsumer, Consumer<OrderStatus> orderStatusConsumer,
            Consumer<LocalDateTime> orderTimestampConsumer) {

        log.debug("placing otder");
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
            orderStatus = ExtractExceptions.translate(e);
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
            log.debug("Verify Order");
            tradeService.verifyOrder(limitOrder);
            orderStatus = new OrderStatus(PLACED, "");
            //log.debug("order placed {}.", orderStatus.getOrderStatusKind());
        } catch (Exception e) {
            log.error("failed to place order in test:\n type: {}, \nreason: {}. \n, \ntrace: {}.",
                    e.getClass().getCanonicalName(),
                    e.getMessage(),
                    e.getStackTrace());
            orderStatus = ExtractExceptions.translate(e);
        }

        orderStatusConsumer.accept(orderStatus);
        orderTimestampConsumer.accept(LocalDateTime.now(UTC));

        return orderStatus;
            }
}
