package org.altfund.xchangeinterface.xchange.service.util;

import org.altfund.xchangeinterface.xchange.service.util.JsonifyExceptions;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.altfund.xchangeinterface.util.JsonHelper;

@Slf4j
public class JsonifyTradeFees {

    public static ObjectNode toJson(
            Map<CurrencyPair, CurrencyPairMetaData> currencyPairs,
            String exchange,
            JsonHelper jh) throws XChangeServiceException {

        ObjectNode errorMap = jh.getObjectNode();
        ObjectNode json = jh.getObjectNode();
        ObjectNode innerJson;

        try {
            for (Map.Entry<CurrencyPair, CurrencyPairMetaData> entry : currencyPairs.entrySet()) {
                innerJson = jh.getObjectNode();
                innerJson.put("max", entry.getValue().getMaximumAmount());
                innerJson.put("min", entry.getValue().getMinimumAmount());
                innerJson.put("priceScale", entry.getValue().getPriceScale());
                innerJson.put("tradeFee", entry.getValue().getTradingFee());
                json.put(entry.getKey().toString(), innerJson);
            }

        } catch (RuntimeException re) {
            log.error("Non-retryable error occurred while processing exchange {}.",
                    exchange);
            errorMap.put("ERROR", exchange + "Falied to retrieve contents of exchange");
            return errorMap;
        }
        return json;
    }
}
