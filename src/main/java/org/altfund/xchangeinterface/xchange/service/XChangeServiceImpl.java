package org.altfund.xchangeinterface.xchange.service;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.altfund.xchangeinterface.xchange.model.Exchange;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import java.util.Map;
import java.util.TreeMap;
import java.util.Optional;
import java.util.NoSuchElementException;

/**
 * altfund
 */
@Slf4j
public class XChangeServiceImpl implements XChangeService {

    private final XChangeFactory xChangeFactory;

    public XChangeServiceImpl(XChangeFactory xChangeFactory) {
        this.xChangeFactory = xChangeFactory;
    }

    @Override
    public Map<String, String> getExchangeCurrencies(String exchange) { // throws XChangeServiceException
        Optional<ExchangeMetaData>  metaData;
        Map<String, String> currencyMap;
        try {
            metaData = Optional.ofNullable(xChangeFactory.getExchangeMetaData(exchange));
            if (!metaData.isPresent()){
                currencyMap = new TreeMap<>();
                currencyMap.put("ERROR", "No such exchange: " + exchange);
                return currencyMap;
            }

            currencyMap =  jsonifyCurrencies(metaData.get().getCurrencies(), exchange);
        } catch (XChangeServiceException ex) {
            // import java.time.LocalDateTime;
            currencyMap = new TreeMap<>();
            currencyMap.put("ERROR", ex.getMessage());
        }
        return currencyMap;
    }

    private Map<String, String> jsonifyCurrencies(Map<Currency, CurrencyMetaData> currencies, String exchange) {
        Map<String, String> currencyMap = new TreeMap<>();
        Optional<String> currencyString;
        Optional<String> currencyDisplayName;
        String key;
        String value;

        try {
            for (Map.Entry<Currency, CurrencyMetaData> entry : currencies.entrySet()) {
                key = "";
                value = "";

                try {
                    currencyString = Optional.ofNullable(entry.getKey().getCurrencyCode());
                    currencyDisplayName = Optional.ofNullable(entry.getKey().getDisplayName());

                    key = currencyString.orElse("");
                    value = currencyDisplayName.orElse("");
                } catch(NoSuchElementException e) {
                    log.error("No currency code found from currency ", key);
                }

                currencyMap.put(key, value);
                //currencyMap.put("currency", entry.getKey().getDisplayName();
            }
            log.info("Processed exchange currency {} successfully.", exchange);
        } catch (RuntimeException re) {
            log.error("Non-retryable error occurred while processing exchange {}.",
                    exchange);
            currencyMap.put("ERROR, Falied to retrieve contents of exchange", exchange );
            //TODO return an error;
        }
        return currencyMap;
    }
}
