package org.altfund.xchangeinterface.xchange.service.util;

import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.TreeMap;
import java.util.Optional;
import java.util.NoSuchElementException;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.meta.CurrencyMetaData;

@Slf4j
public class JsonifyCurrencies {
    public static Map<String, String> toJson(Map<Currency, CurrencyMetaData> currencies, String exchange) {
        Map<String, String> json = new TreeMap<>();
        Map<String, String> errorMap = new TreeMap<>();
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
                    //TODO put errorMap call here
                }

                json.put(key, value);
            }
            log.info("Processed exchange currency {} successfully.", exchange);
        } catch (RuntimeException re) {
            log.error("Non-retryable error occurred while processing exchange {}.",
                    exchange);
            errorMap.put("ERROR, Falied to retrieve contents of exchange", exchange );
            return errorMap;
        }
        return json;
    }
}
