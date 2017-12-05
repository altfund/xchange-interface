package org.altfund.xchangeinterface.xchange.service.util;

import org.altfund.xchangeinterface.xchange.service.util.ExtractExceptions;
import org.altfund.xchangeinterface.xchange.service.util.OrderBookSquasher;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.altfund.xchangeinterface.xchange.model.MarketByExchanges;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.altfund.xchangeinterface.util.JsonHelper;
import org.altfund.xchangeinterface.util.KWayMerge;

@Slf4j
public class ExtractOrderBooks {

    public static ObjectNode toJson(
            MarketDataService marketDataService,
            Map<String, String> params,
            JsonHelper jh) throws XChangeServiceException {

        ObjectNode errorMap = jh.getObjectNode();
        ObjectNode json = jh.getObjectNode();
        ObjectNode innerJson;
        CurrencyPair cp = null;

        try {
            innerJson = jh.getObjectNode();
            cp = new CurrencyPair(
                    params.get("base_currency"),
                    params.get("quote_currency")
                    );
            log.debug("currency pair submitted to order book {}.", cp.toString());

            try {
                innerJson = jh.getObjectMapper().convertValue(getOrderBook(marketDataService, cp, params.get("exchange")), ObjectNode.class);
                json.put(cp.toString(), innerJson);
            } catch (Exception e) {
                json.put(cp.toString(), ExtractExceptions.toJson(e, jh));
            }

        } catch (RuntimeException re) {
            log.error("Non-retryable error occurred while processing exchange {}.",
                    params.get( "exchange" ));
            errorMap.put("ERROR","Falied to retrieve contents of exchange " + params.get("exchange"));
            return errorMap;
        }
        return json;
    }

    public static OrderBook raw (
                        MarketDataService marketDataService,
                        CurrencyPair cp,
                        String exchange)  throws Exception {
        OrderBook orderBook = null;

        try {
            log.debug("{} currency pair submitted to order book {}.", exchange, cp.toString());

            try {
                orderBook = getOrderBook(marketDataService, cp, exchange);
                log.debug("Got order book for exchange {} market {}.", exchange, cp.toString());
            } catch (Exception e) {
                log.debug("Failed to get order book for exchange {} market {}.", exchange, cp.toString());
                throw e;
            }

        } catch (RuntimeException re) {
            log.error("Non-retryable error occurred while processing exchange {}.", exchange);
            throw re;
        }
        return orderBook;
    }

    private static OrderBook getOrderBook(MarketDataService marketDataService, CurrencyPair cp, String exchange) throws Exception{
        //TODO add exchange specific logic to get order book if it is unaggregated.
        if (exchange.toLowerCase().equals("gdax")) {
            log.debug("\n\n\n\n\n\nGDAX ORDER BOOK AGGREGATION");
            return OrderBookSquasher.byPrice(marketDataService.getOrderBook(cp));
        }
        return marketDataService.getOrderBook(cp);
    }
}
