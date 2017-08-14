package org.altfund.xchangeinterface.xchange.service;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.altfund.xchangeinterface.util.JsonHelper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.altfund.xchangeinterface.xchange.model.Exchange;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.service.marketdata.MarketDataService;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.math.BigDecimal;

/**
 * altfund
 */
@Slf4j
public class XChangeServiceImpl implements XChangeService {

    private final XChangeFactory xChangeFactory;
    private final JsonHelper jh;

    public XChangeServiceImpl(XChangeFactory xChangeFactory, JsonHelper jh) {
        this.xChangeFactory = xChangeFactory;
        this.jh = jh;
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

    public ObjectNode getTickers(String exchange) {
        Optional<List<CurrencyPair>>  currencyPairs;
        Optional<MarketDataService>  marketDataService;
        ObjectNode tickerMap = jh.getObjectNode();
        ObjectNode errorMap = jh.getObjectNode();

        try {
            xChangeFactory.setProperties(exchange);
            currencyPairs = Optional.ofNullable(xChangeFactory.getExchangeSymbols(exchange));
            if (!currencyPairs.isPresent()){
                errorMap.put("ERROR", "No such exchange " + exchange);
                return errorMap;
            }

            marketDataService = Optional.ofNullable(xChangeFactory.getMarketDataService(exchange));
            if (!marketDataService.isPresent()){
                errorMap.put("ERROR", "No such exchange " + exchange);
                return errorMap;
            }

            tickerMap =  jsonifyExchangeTickers(currencyPairs.get(), marketDataService.get(), exchange);
        } catch (XChangeServiceException ex) {
            // import java.time.LocalDateTime;
            errorMap.put("ERROR", ex.getMessage());
            return errorMap;
        }
        return tickerMap;
    }

    public ObjectNode getOrderBooks(Map<String, String> params) {
        Optional<MarketDataService>  marketDataService;
        ObjectNode orderBookMap = jh.getObjectNode();
        ObjectNode errorMap = jh.getObjectNode();

        try {
            xChangeFactory.setProperties(params.get("exchange"));
            marketDataService = Optional.ofNullable(xChangeFactory.getMarketDataService(params.get( "exchange" )));
            if (!marketDataService.isPresent()){
                errorMap.put("ERROR", "No such exchange " + params.get( "exchange" ));
                return errorMap;
            }

            //params for this method are needed because it has "base_currency" and "quote_currency"
            orderBookMap =  jsonifyOrderBooks(marketDataService.get(), params);
        } catch (XChangeServiceException ex) {
            // import java.time.LocalDateTime;
            errorMap.put("ERROR", ex.getMessage());
            return errorMap;
        }
        return orderBookMap;
    }

    @Override
    public ObjectNode getExchangeTradeFees(Map<String, String> params) {
        Optional<ExchangeMetaData>  metaData;
        ObjectNode tradeMap = jh.getObjectNode();
        ObjectNode errorMap = jh.getObjectNode();

        try {
            xChangeFactory.setProperties(params.get("exchange"));
            metaData = Optional.ofNullable(xChangeFactory.getExchangeMetaData(params.get("exchange")));
            if (!metaData.isPresent()){
                errorMap.put("ERROR", "No such exchange " + params.get("exchange"));
                return errorMap;
            }

            tradeMap =  jsonifyTradeFees(metaData.get().getCurrencyPairs(), params.get("exchange"));
        } catch (XChangeServiceException ex) {
            // import java.time.LocalDateTime;
            errorMap.put("ERROR", ex.getMessage());
            return errorMap;
        }
        return tradeMap;
    }

    @Override
    public ObjectNode getExchangeBalances(Map<String, String> params) {
        Optional<AccountService> accountService;
        Optional<AccountInfo> accountInfo;
        Optional<Map<String, Wallet>> wallets;
        Optional<BigDecimal> tradingFee;
        ObjectNode balanceMap = jh.getObjectNode();
        ObjectNode errorMap = jh.getObjectNode();

        try {
            xChangeFactory.setProperties(params);
            accountService = Optional.ofNullable(xChangeFactory.getAccountService(params.get("exchange")));
            if (!accountService.isPresent()){
                errorMap.put("ERROR", params.get("exchange") + "No such account service");
                return errorMap;
            }

            try {
                accountInfo = Optional.ofNullable(accountService.get().getAccountInfo());
                if (!accountInfo.isPresent()){
                    errorMap.put("ERROR", params.get("exchange") + "No such account info");
                    return errorMap;
                }
            } catch (Exception ex) {
                errorMap.put("ERROR", params.get("exchange") + ex.toString() + ": " + ex.getMessage());
                return errorMap;
            }

            wallets = Optional.ofNullable(accountInfo.get().getWallets());
            if (!wallets.isPresent()){
                errorMap.put("ERROR", params.get("exchange") + "No such wallets");
                return errorMap;
            }

            balanceMap = jsonifyBalances(wallets.get(), params.get("exchange"));
        } catch (XChangeServiceException ex) {
            // import java.time.LocalDateTime;
            errorMap.put("ERROR", params.get("exchange") + ex.toString() + ": " + ex.getMessage());
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

    private ObjectNode jsonifyExchangeTickers(
            List<CurrencyPair> currencyPairs,
            MarketDataService marketDataService,
            String exchange) throws XChangeServiceException {

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
                    json.put(cp.toString(), translateException(e));
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

    private ObjectNode jsonifyOrderBooks(
            MarketDataService marketDataService,
            Map<String, String> params) throws XChangeServiceException {

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
                json.put(cp.toString(), translateException(e));
            }

        } catch (RuntimeException re) {
            log.error("Non-retryable error occurred while processing exchange {}.",
                    params.get( "exchange" ));
            errorMap.put("ERROR","Falied to retrieve contents of exchange " + params.get("exchange"));
            return errorMap;
        }
        return json;
    }

    private ObjectNode jsonifyTradeFees(Map<CurrencyPair, CurrencyPairMetaData> currencyPairs, String exchange) {
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

    private ObjectNode jsonifyBalances(Map<String, Wallet> wallets, String exchange) {
        ObjectNode innerJson = jh.getObjectNode();
        ObjectNode outerJson = jh.getObjectNode();
        ObjectNode errorMap = jh.getObjectNode();
        ObjectNode json;
        Optional<String> walletString;
        Optional<String> walletId;
        Optional<String> walletName;
        Optional<Wallet> wallet;
        String key;
        Optional<Currency> currency;
        Optional<Balance> balance;
        //Optional<String> walletValue;
        String currencyCode;
        String balanceAvailable;

        try {
            for (Map.Entry<String, Wallet> entry : wallets.entrySet()) {
                key = "";
                try {
                    wallet = Optional.ofNullable(entry.getValue());
                    walletName = Optional.ofNullable(wallet.get().getName());
                    walletString = Optional.ofNullable(entry.getKey());
                    walletId = Optional.ofNullable(wallet.get().getId());

                    key = walletName.orElse(
                            walletString.orElse(
                                walletId.orElse("wallet")
                                )
                            );
                    currencyCode = "";
                    balanceAvailable = "";
                    if (wallet.isPresent()){
                        for (Map.Entry<Currency, Balance> balanceEntry : wallet.get().getBalances().entrySet()) {
                            currencyCode = "";
                            currency = Optional.ofNullable(balanceEntry.getKey());
                            balance = Optional.ofNullable(balanceEntry.getValue());
                            json = getWalletBalances(currency, balance);
                            if (currency.isPresent())
                                currencyCode = currency.get().getCurrencyCode();
                            innerJson.put(currencyCode, json);


                            log.info("a balance " + json.toString());
                        }//end loop for balances of currency
                        outerJson.put(key, innerJson);
                    } else {
                        errorMap.put("ERROR", "no wallets");
                        return errorMap;
                    }//no loop needed for balances
                    outerJson.put(key, innerJson);
                } catch(NoSuchElementException e) {
                    log.error("No balances found for wallet ", key);
                    errorMap.put("ERROR", exchange + "Falied to retrieve contents of wallets in exchange");
                    return errorMap;
                }
            } //end wallet loop
            log.info("Processed exchange currency {} successfully.", exchange);
        } catch (RuntimeException re) {
            log.error("Non-retryable error occurred while processing exchange {}.",
                    exchange);
            errorMap.put("ERROR", exchange + "Falied to retrieve contents of exchange");
            return errorMap;
        }
        return outerJson;
    }
    private ObjectNode getWalletBalances(Optional<Currency> currency, Optional<Balance> balance){
        String currencyCode = "";
        ObjectNode json = jh.getObjectNode();
        ObjectNode outerJson = jh.getObjectNode();
        if (balance.isPresent()) {
            json.put("available", balance.get().getAvailable());
            json.put("availableForWithdraw", balance.get().getAvailableForWithdrawal());
            json.put("borrowed", balance.get().getBorrowed());
            json.put("depositing", balance.get().getDepositing());
            json.put("frozen", balance.get().getFrozen());
            json.put("loaned", balance.get().getLoaned());
            json.put("total", balance.get().getTotal());
            json.put("withdrawing", balance.get().getWithdrawing());
        }
        return json;
    }

    private ObjectNode translateException(Exception e) {
        ObjectNode errorMap = jh.getObjectNode();
        if (e instanceof IOException) {
            // Orders failed due to a network error can be retried.
            errorMap.put("ERROR", "Indication that a networking error occurred while fetching JSON data while fetching requested data on exchange " );
            return errorMap;
        } else if (e instanceof ExchangeException) {
            errorMap.put("ERROR", "Indication that the exchange reported some kind of error with the request or response while fetching requested data on exchange " );
            return errorMap;
        } else if (e instanceof IllegalArgumentException) {
            errorMap.put("ERROR", "Illegal argument exception while fetching requested data on exchange " );
            return errorMap;
        } else if (e instanceof NotAvailableFromExchangeException) {
            errorMap.put("ERROR", "Indication that the exchange does not support the requested function or data while fetching requested data on exchange " );
            return errorMap;
        } else if (e instanceof NotYetImplementedForExchangeException) {
            errorMap.put("ERROR", "Indication that the exchange supports the requested function or data, but it has not yet been implemented while fetching requested data on exchange " );
            return errorMap;
        } else {
            errorMap.put("ERROR", "Unknown error while fetching requested data on exchange " );
            return errorMap;
        }
    }

}
