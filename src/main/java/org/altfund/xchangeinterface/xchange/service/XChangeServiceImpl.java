package org.altfund.xchangeinterface.xchange.service;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.altfund.xchangeinterface.xchange.model.Exchange;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.math.BigDecimal;

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
    public Map<String, String> getExchangeCurrencies(String exchange) {
        Optional<ExchangeMetaData>  metaData;
        Map<String, String> currencyMap;
        Map<String, String> errorMap;
        try {
            xChangeFactory.setProperties(exchange);
            metaData = Optional.ofNullable(xChangeFactory.getExchangeMetaData(exchange));
            if (!metaData.isPresent()){
                errorMap = new TreeMap<>();
                errorMap.put("ERROR", "No such exchange " + exchange);
                return errorMap;
            }

            currencyMap =  jsonifyCurrencies(metaData.get().getCurrencies(), exchange);
        } catch (XChangeServiceException ex) {
            // import java.time.LocalDateTime;
            errorMap = new TreeMap<>();
            errorMap.put("ERROR", ex.getMessage());
            return errorMap;
        }
        return currencyMap;
    }

    @Override
    public Map<String, Map<String, String>> getExchangeBalances(Map<String, String> params) {
        Optional<AccountService> accountService;
        Optional<AccountInfo> accountInfo;
        Optional<Map<String, Wallet>> wallets;
        Map<String, Map<String, String>> balanceMap;
        Map<String, Map<String, String>> errorMap;
        Map<String, String> nestedErrorMap;
        errorMap = new HashMap<>();

        try {
            balanceMap = new HashMap<>();
            xChangeFactory.setProperties(params);
            accountService = Optional.ofNullable(xChangeFactory.getAccountService(params.get("exchange")));
            if (!accountService.isPresent()){
                errorMap.put("ERROR", new HashMap(){{put(params.get("exchange"),"No such account service");}});
                return errorMap;
            }

            try {
                accountInfo = Optional.ofNullable(accountService.get().getAccountInfo());
                if (!accountInfo.isPresent()){
                    errorMap.put("ERROR", new HashMap(){{put(params.get("exchange"), "No such account info");}});
                    return errorMap;
                }
            } catch (Exception ex) {
                errorMap.put("ERROR", new HashMap(){{put(params.get("exchange"), ex.toString() + ": " + ex.getMessage());}});
                return errorMap;
            }

            wallets = Optional.ofNullable(accountInfo.get().getWallets());
            if (!wallets.isPresent()){
                errorMap.put("ERROR", new HashMap(){{put(params.get("exchange"), "No such wallets");}});
                return errorMap;
            }

            balanceMap = jsonifyBalances(wallets.get(), params.get("exchange"));
        } catch (XChangeServiceException ex) {
            // import java.time.LocalDateTime;
            errorMap.put("ERROR", new HashMap(){{put(params.get("exchange"), ex.toString() + ": " + ex.getMessage());}});
            return errorMap;
        }
        log.debug("balancemap " + balanceMap);
        return balanceMap;
    }

    private Map<String, String> jsonifyCurrencies(Map<Currency, CurrencyMetaData> currencies, String exchange) {
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

    private Map<String, Map<String, String>> jsonifyBalances(Map<String, Wallet> wallets, String exchange) {
        Map<String, Map<String, String>> json = new HashMap<>();
        Map<String, String> nestedJson = new HashMap<>();
        Map<String, Map<String, String>> errorMap = new HashMap<>();
        Optional<String> walletString;
        Optional<Wallet> wallet;
        Optional<String> walletName;
        String key;
        String value;
        Optional<Currency> currency;
        Optional<Balance> balance;
        Optional<String> walletValue;
        String currencyCode;
        String balanceAvailable;

        try {
            for (Map.Entry<String, Wallet> entry : wallets.entrySet()) {
                key = "";
                value = "";

                try {
                    walletString = Optional.ofNullable(entry.getKey());
                    wallet = Optional.ofNullable(entry.getValue());
                    walletName = Optional.ofNullable(entry.getValue().getName());

                    key = walletString.orElse("");
                    value = walletName.orElse("");
                    currencyCode = "";
                    balanceAvailable = "";
                    if (wallet.isPresent()){
                        for (Map.Entry<Currency, Balance> balanceEntry : wallet.get().getBalances().entrySet()) {
                            currencyCode = "";
                            balanceAvailable = "";
                            currency = Optional.ofNullable(balanceEntry.getKey());
                            balance = Optional.ofNullable(balanceEntry.getValue());
                            if (currency.isPresent())
                                currencyCode = currency.get().getCurrencyCode();
                            if (balance.isPresent())
                                balanceAvailable = balance.get().getAvailable().toString();

                            //TODO getName or getId?
                            nestedJson.put(currencyCode, balanceAvailable);

                            walletValue = Optional.ofNullable(entry.getValue().getId());
                            if(walletValue.isPresent()){
                                json.put(walletValue.get(), nestedJson);
                            } else {
                                json.put("wallet", nestedJson);
                            }
                        }
                    } else {
                        nestedJson.put(currencyCode, balanceAvailable);
                        json.put("ERROR", nestedJson);
                    }
                } catch(NoSuchElementException e) {
                    log.error("No currency code found from currency ", key);
                }
            }
            log.info("Processed exchange currency {} successfully.", exchange);
        } catch (RuntimeException re) {
            log.error("Non-retryable error occurred while processing exchange {}.",
                    exchange);
            errorMap.put("ERROR", new HashMap(){{put(exchange, "Falied to retrieve contents of exchange");}});
            return errorMap;
        }
        return json;
    }
}
