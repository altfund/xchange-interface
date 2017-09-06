package org.altfund.xchangeinterface.xchange.service;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.altfund.xchangeinterface.xchange.model.ExchangeCredentials;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.altfund.xchangeinterface.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.altfund.xchangeinterface.xchange.model.Exchange;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.altfund.xchangeinterface.xchange.service.util.JsonifyCurrencies;
import org.altfund.xchangeinterface.xchange.service.util.JsonifyExchangeTickers;
import org.altfund.xchangeinterface.xchange.service.util.JsonifyOrderBooks;
import org.altfund.xchangeinterface.xchange.service.util.JsonifyTradeFees;
import org.altfund.xchangeinterface.xchange.service.util.JsonifyBalances;
import java.util.List;
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
    private final JsonHelper jh;

    public XChangeServiceImpl(XChangeFactory xChangeFactory, JsonHelper jh) {
        this.xChangeFactory = xChangeFactory;
        this.jh = jh;
    }

    @Override
    public Map<String, String> getExchangeCurrencies(String exchange) {
        Optional<ExchangeMetaData>  metaData;
        Map<String, String> currencyMap;
        Map<String, String> errorMap = new TreeMap<>();
        try {
            //xChangeFactory.setProperties(exchange);
            metaData = Optional.ofNullable(xChangeFactory.getExchangeMetaData(exchange));
            if (!metaData.isPresent()){
                errorMap.put("ERROR", "No such exchange " + exchange);
                return errorMap;
            }

            currencyMap =  JsonifyCurrencies.toJson(metaData.get().getCurrencies(), exchange);
        }
        catch (XChangeServiceException ex) {
            // import java.time.LocalDateTime;
            errorMap = new TreeMap<>();
            errorMap.put("ERROR", ex.getMessage());
            return errorMap;
        }
        catch (IOException ex) {
            // import java.time.LocalDateTime;
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
            //xChangeFactory.setProperties(exchange);
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

            tickerMap =  JsonifyExchangeTickers.toJson(currencyPairs.get(), marketDataService.get(), exchange, jh);
        }
        catch (IOException ex) {
            // import java.time.LocalDateTime;
            errorMap.put("ERROR", ex.getMessage());
            return errorMap;
        }
        catch (XChangeServiceException ex) {
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
            //xChangeFactory.setProperties(params.get("exchange"));
            marketDataService = Optional.ofNullable(xChangeFactory.getMarketDataService(params.get( "exchange" )));
            if (!marketDataService.isPresent()){
                errorMap.put("ERROR", "No such exchange " + params.get( "exchange" ));
                return errorMap;
            }

            //params for this method are needed because it has "base_currency" and "quote_currency"
            orderBookMap =  JsonifyOrderBooks.toJson(marketDataService.get(), params, jh);
        }
        catch (XChangeServiceException ex) {
            // import java.time.LocalDateTime;
            errorMap.put("ERROR", ex.getMessage());
            return errorMap;
        }
        catch (IOException ex) {
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
            //xChangeFactory.setProperties(params.get("exchange"));
            metaData = Optional.ofNullable(xChangeFactory.getExchangeMetaData(params.get("exchange")));
            if (!metaData.isPresent()){
                errorMap.put("ERROR", "No such exchange " + params.get("exchange"));
                return errorMap;
            }

            tradeMap =  JsonifyTradeFees.toJson(metaData.get().getCurrencyPairs(), params.get("exchange"), jh);
        }
        catch (XChangeServiceException ex) {
            // import java.time.LocalDateTime;
            errorMap.put("ERROR", ex.getMessage());
            return errorMap;
        }
        catch (IOException ex) {
            // import java.time.LocalDateTime;
            errorMap.put("ERROR", ex.getMessage());
            return errorMap;
        }
        return tradeMap;
    }

    @Override
    public ObjectNode getExchangeBalances(ExchangeCredentials exchangeCredentials) {
        Optional<AccountService> accountService;
        Optional<AccountInfo> accountInfo;
        Optional<Map<String, Wallet>> wallets;
        Optional<BigDecimal> tradingFee;
        ObjectNode balanceMap = jh.getObjectNode();
        ObjectNode errorMap = jh.getObjectNode();

        try {
            //xChangeFactory.setProperties(exchangeCredentials);
            accountService = Optional.ofNullable(xChangeFactory.getAccountService(exchangeCredentials));
            if (!accountService.isPresent()){
                errorMap.put("ERROR", exchangeCredentials.getExchange() + "No such account service");
                return errorMap;
            }

            try {
                accountInfo = Optional.ofNullable(accountService.get().getAccountInfo());
                if (!accountInfo.isPresent()){
                    errorMap.put("ERROR", exchangeCredentials.getExchange() + "No such account info");
                    return errorMap;
                }
            } catch (Exception ex) {
                errorMap.put("ERROR", exchangeCredentials.getExchange() + ex.toString() + ": " + ex.getMessage());
                return errorMap;
            }

            wallets = Optional.ofNullable(accountInfo.get().getWallets());
            if (!wallets.isPresent()){
                errorMap.put("ERROR", exchangeCredentials.getExchange() + "No such wallets");
                return errorMap;
            }

            balanceMap = JsonifyBalances.toJson(wallets.get(), exchangeCredentials.getExchange(), jh);
        }
        catch (XChangeServiceException ex) {
            // import java.time.LocalDateTime;
            errorMap.put("ERROR", exchangeCredentials.getExchange() + ex.toString() + ": " + ex.getMessage());
            return errorMap;
        }
        catch (IOException ex) {
            // import java.time.LocalDateTime;
            errorMap.put("ERROR", ex.getMessage());
            return errorMap;
        }
        log.debug("balancemap " + balanceMap);
        return balanceMap;
    }
}
