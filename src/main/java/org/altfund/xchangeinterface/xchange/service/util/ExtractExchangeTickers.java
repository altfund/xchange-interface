package org.altfund.xchangeinterface.xchange.service.util;

import org.altfund.xchangeinterface.xchange.service.util.ExtractExceptions;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.knowm.xchange.currency.CurrencyPair;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.altfund.xchangeinterface.util.JsonHelper;

@Slf4j
public class ExtractExchangeTickers {

    public static ObjectNode toJson(
            List<CurrencyPair> currencyPairs,
            MarketDataService marketDataService,
            String exchange,
            JsonHelper jh) throws XChangeServiceException {

        ObjectNode errorMap = jh.getObjectNode();
        ObjectNode json = jh.getObjectNode();
        ObjectNode innerJson;
        Ticker ticker = null;

        try {
            for (CurrencyPair cp : currencyPairs) {
                innerJson = jh.getObjectNode();

                try {
                    ticker = marketDataService.getTicker(cp);
                    innerJson = jh.getObjectMapper().convertValue(ticker, ObjectNode.class);
                    json.put(cp.toString(), innerJson);
                } catch (Exception e) {
                    json.put(cp.toString(), ExtractExceptions.toJson(e, jh));
                }
            }

        } catch (RuntimeException re) {
            log.error("Non-retryable error occurred while processing exchange {}.",
                    exchange);
            errorMap.put("ERROR","Falied to retrieve contents of exchange " + exchange );
            return errorMap;
        }
        return json;
    }
}
