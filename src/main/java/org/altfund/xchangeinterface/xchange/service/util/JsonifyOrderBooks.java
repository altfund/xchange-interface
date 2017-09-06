package org.altfund.xchangeinterface.xchange.service.util;

import org.altfund.xchangeinterface.xchange.service.util.JsonifyExceptions;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.altfund.xchangeinterface.util.JsonHelper;

@Slf4j
public class JsonifyOrderBooks {

    public static ObjectNode toJson(
            MarketDataService marketDataService,
            Map<String, String> params,
            JsonHelper jh) throws XChangeServiceException {

        ObjectNode errorMap = jh.getObjectNode();
        ObjectNode json = jh.getObjectNode();
        ObjectNode innerJson;
        OrderBook orderBook = null;
        CurrencyPair cp = null;

        try {
            innerJson = jh.getObjectNode();
            cp = new CurrencyPair(
                    params.get("quote_currency"),
                    params.get("base_currency")
                    );
            log.debug("currency pair submitted to order book {}.", cp.toString());

            try {
                orderBook = marketDataService.getOrderBook(cp);
                innerJson = jh.getObjectMapper().convertValue(orderBook, ObjectNode.class);
                json.put(cp.toString(), innerJson);
            } catch (Exception e) {
                json.put(cp.toString(), JsonifyExceptions.toJson(e, jh));
            }

        } catch (RuntimeException re) {
            log.error("Non-retryable error occurred while processing exchange {}.",
                    params.get( "exchange" ));
            errorMap.put("ERROR","Falied to retrieve contents of exchange " + params.get("exchange"));
            return errorMap;
        }
        return json;
    }
}
