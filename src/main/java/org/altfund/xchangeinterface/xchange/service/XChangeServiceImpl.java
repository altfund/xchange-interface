package org.altfund.xchangeinterface.xchange.service;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Optional;
import java.math.BigDecimal;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;

import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.altfund.xchangeinterface.xchange.model.GetOrdersParams;
import org.altfund.xchangeinterface.xchange.model.OrderStatus;
import org.altfund.xchangeinterface.xchange.model.OrderStatusTypes;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.PLACED;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.CANCELED;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.CANCEL_FAILED;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.PROCESSING_FAILED;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsAll;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParams;

import org.altfund.xchangeinterface.util.JsonHelper;
import org.altfund.xchangeinterface.util.KWayMerge;
import org.altfund.xchangeinterface.xchange.model.Exchange;
import org.altfund.xchangeinterface.xchange.model.CurrenciesOnExchange;
import org.altfund.xchangeinterface.xchange.model.ExchangeCredentials;
import org.altfund.xchangeinterface.xchange.model.LimitOrderExchange;
import org.altfund.xchangeinterface.xchange.model.MarketByExchanges;
import org.altfund.xchangeinterface.xchange.model.OpenOrder;
import org.altfund.xchangeinterface.xchange.model.OrderSpec;
import org.altfund.xchangeinterface.xchange.model.Order;
import org.altfund.xchangeinterface.xchange.model.OrderResponse;
import org.altfund.xchangeinterface.xchange.model.TradeHistory;
import org.altfund.xchangeinterface.xchange.model.TradeHistoryParams;
import org.altfund.xchangeinterface.xchange.service.util.ExtractCurrencies;
import org.altfund.xchangeinterface.xchange.service.util.ExtractExchangeTickers;
import org.altfund.xchangeinterface.xchange.service.util.ExtractOrderBooks;
import org.altfund.xchangeinterface.xchange.service.util.ExtractTradeFees;
import org.altfund.xchangeinterface.xchange.service.util.ExtractBalances;
import org.altfund.xchangeinterface.xchange.service.util.LimitOrderPlacer;

import java.io.IOException;
import java.lang.NoSuchMethodException;
import java.util.NoSuchElementException;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;

//reflection
import java.lang.reflect.Method;

/**
 * altfund
 */
@Slf4j
public class XChangeServiceImpl implements XChangeService {

    private final XChangeFactory xChangeFactory;
    private final JsonHelper jh;
    private final KWayMerge kWayMerge;
    private final LimitOrderPlacer limitOrderPlacer;
    private final DozerBeanMapper dozerBeanMapper;

    public XChangeServiceImpl(XChangeFactory xChangeFactory,
            JsonHelper jh,
            LimitOrderPlacer limitOrderPlacer,
            DozerBeanMapper dozerBeanMapper,
            KWayMerge kWayMerge) {
        this.xChangeFactory = xChangeFactory;
        this.jh = jh;
        this.limitOrderPlacer = limitOrderPlacer;
        this.dozerBeanMapper = dozerBeanMapper;
        this.kWayMerge = kWayMerge;
    }

    @Override
    public ObjectNode getExchangeCurrencies(String exchange) {
        Optional<ExchangeMetaData>  metaData;
        ObjectNode currencyMap = jh.getObjectNode();
        ObjectNode errorMap = jh.getObjectNode();

        try {
            //xChangeFactory.setProperties(exchange);
            metaData = Optional.ofNullable(xChangeFactory.getExchangeMetaData(exchange));
            if (!metaData.isPresent()){
                errorMap.put("ERROR", "No such exchange " + exchange);
                return errorMap;
            }

            currencyMap =  ExtractCurrencies.toJson(metaData.get().getCurrencies(), exchange, jh);
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

            tickerMap =  ExtractExchangeTickers.toJson(currencyPairs.get(), marketDataService.get(), exchange, jh);
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
            orderBookMap =  ExtractOrderBooks.toJson(marketDataService.get(), params, jh);
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
    public String getAggregateOrderBooks(MarketByExchanges marketByExchanges) throws Exception {
        List<OrderBook> books = null;
        MarketDataService marketDataService = null;
        List<String> exchanges = marketByExchanges.getExchanges();
        ArrayList<List<LimitOrder>> asks = new ArrayList<List<LimitOrder>>();
        ArrayList<String> askExchanges = new ArrayList<String>();
        ArrayList<List<LimitOrder>> bids = new ArrayList<List<LimitOrder>>();
        ArrayList<String> bidExchanges = new ArrayList<String>();
        List<LimitOrderExchange> aggregatedAsks = new ArrayList<LimitOrderExchange>();
        List<LimitOrderExchange> aggregatedBids = new ArrayList<LimitOrderExchange>();
        OrderBook ob = null;
        ObjectNode orderBookMap = jh.getObjectNode();
        ObjectNode errorMap = jh.getObjectNode();

        CurrencyPair cp = new CurrencyPair(
                marketByExchanges.getBaseCurrency(),
                marketByExchanges.getQuoteCurrency());

        try {
            log.debug("Begin extract MarketDataService(s)");
            for (int i = 0; i < exchanges.size(); i++) {
                log.debug("Get MarketDataService for {}", exchanges.get(i));
                marketDataService = xChangeFactory.getMarketDataService(exchanges.get(i));
                ob = ExtractOrderBooks.raw(marketDataService, cp, exchanges.get(i));

                asks.add(ob.getAsks());
                askExchanges.add(exchanges.get(i));
                bids.add(ob.getBids());
                bidExchanges.add(exchanges.get(i));
            }
            aggregatedAsks = kWayMerge.mergeKLists(asks, askExchanges);
            aggregatedBids = kWayMerge.mergeKLists(bids, bidExchanges);
            orderBookMap.put("ASKS", jh.getObjectMapper().writeValueAsString(aggregatedAsks));
            orderBookMap.put("BIDS", jh.getObjectMapper().writeValueAsString(aggregatedBids));
            //params for this method are needed because it has "base_currency" and "quote_currency"
        }
        catch (XChangeServiceException ex) {
            // import java.time.LocalDateTime;
            //log.error("xchangeservice excepti)on SHOULD THROW TO CALLER \n{}", ex.getStackTrace());
            //log.error("{}", ex.getStackTrace());
            //errorMap.put("ERROR", ex.getMessage());
            //return errorMap;
            return jh.getObjectMapper().writeValueAsString(errorMap);
        }
        catch (Exception ex) {
            // import java.time.LocalDateTime;
            //log.error("xchangeservice exception SHOULD THROW TO CALLER \n{}", ex.getStackTrace());
            log.error("{}", ex.getStackTrace());
            //errorMap.put("ERROR", ex.getMessage());
            //return errorMap;
            throw ex;
        }
        //return orderBookMap;
        return jh.getObjectMapper().writeValueAsString(orderBookMap);
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

            tradeMap =  ExtractTradeFees.toJson(metaData.get().getCurrencyPairs(), params.get("exchange"), jh);
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
        ObjectNode balanceMap = jh.getObjectNode();
        ObjectNode errorMap = jh.getObjectNode();

        try {
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

            balanceMap = ExtractBalances.toJson(wallets.get(), exchangeCredentials.getExchange(), jh);
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

    @Override
    public boolean cancelLimitOrder(Order order) throws Exception {
        ExchangeCredentials exchangeCredentials = null;
        ObjectNode errorMap = jh.getObjectNode();
        TradeService tradeService = null;
        exchangeCredentials = order.getExchangeCredentials();
        boolean orderResponse = false;

        try {
            tradeService = xChangeFactory.getTradeService(exchangeCredentials);
            orderResponse = tradeService.cancelOrder(order.getOrderId());
        }
        catch (Exception e) {
            throw e;
        }
        /*
           catch (IOException e) {
           log.error("XChangeServiceException {}: " + e, e.getMessage());
           }
           catch (XChangeServiceException e) {
           log.error("XChangeServiceException {}: " + e, e.getMessage());
           }
           catch (RuntimeException re) {
           log.error("Non-retyable error {}: " + re, re.getMessage());
           }
           */
        return orderResponse;
    }

    @Override
    public String interExchangeArbitrage(List<Order> orders) throws Exception {
        List<OrderResponse> orderResponses = new ArrayList<OrderResponse>();
        OrderResponse prevOrderResponse = null;
        OrderResponse thisOrderResponse = null;
        Order thisOrder = null;
        Order prevOrder = null;
        boolean isCanceled = false;
        String response = "";


        //TODO does not fully support more than 2 orders beause it doesn't cancel the
        //first N orders, only the previous one.
        try {
            for (int i = 0; i < orders.size(); i++) {
                if ((prevOrderResponse != null) && !(prevOrderResponse.getOrderStatus().hasStatus(PLACED))) {
                    prevOrder.setOrderId(prevOrderResponse.getOrderId());
                    isCanceled = cancelLimitOrder(prevOrder);
                    OrderStatusTypes prevOrderStatusType = prevOrderResponse.getOrderStatus().getOrderStatusType();
                    String prevOrderStatusPhrase =  prevOrderResponse.getOrderStatus().getOrderStatusPhrase();
                    if (isCanceled) {
                        prevOrderResponse.getOrderStatus().setOrderStatusType(CANCELED);
                        prevOrderResponse.getOrderStatus().setOrderStatusPhrase("Previously: " + prevOrderStatusType.toString() + ", " + prevOrderStatusPhrase);
                    }
                    else {
                        prevOrderResponse.getOrderStatus().setOrderStatusType(CANCEL_FAILED);
                        prevOrderResponse.getOrderStatus().setOrderStatusPhrase("Previously: " + prevOrderStatusType.toString() + ", " + prevOrderStatusPhrase);
                    }
                    break;
                }
                thisOrder = orders.get(i);
                thisOrderResponse = placeLimitOrder(thisOrder);
                orderResponses.add(thisOrderResponse);
                prevOrderResponse = thisOrderResponse;
                prevOrder = thisOrder;
            }
        }
        catch (Exception e) {
            throw e;
        }
        response = jh.getObjectMapper().writeValueAsString(orderResponses);
        return response;
    }
    @Override
    public String isFeasible(String exchange) throws Exception {
        ExchangeCredentials exchangeCredentials = new ExchangeCredentials(exchange, "bogus", "bogus", "bogus");
        ObjectNode retMap = jh.getObjectNode();
        ObjectNode exMap = jh.getObjectNode();
        String currentMethod = "";

        Method[] methods = XChangeFactory.class.getMethods();
        Class[] parameterTypes = null;

        try {
            for (Method method : methods) {
                currentMethod = method.getName();
                log.debug("curr method {}.", currentMethod);
                Optional<Object> returnValue;
                parameterTypes = method.getParameterTypes();
                log.debug("param types len {}.", parameterTypes.length);
                if (parameterTypes[0] == String.class) {
                    log.debug("string type");
                    returnValue = Optional.ofNullable(method.invoke(xChangeFactory, exchange));
                } else {
                    log.debug("creds type");
                    returnValue = Optional.ofNullable(method.invoke(xChangeFactory, exchangeCredentials));
                }

                if (returnValue.isPresent()) {
                    retMap.put(currentMethod, "true");
                }
                else {
                    retMap.put(currentMethod, "false");
                }
            }
            exMap.put(exchange, retMap);
        }
        catch (Exception e) {
            exMap.put("ERROR", "failed to process " + exchange);
            exMap.put(exchange, retMap);
        }

        /*
           catch (XChangeServiceException ex) {
           retMap.put(currentMethod, "false");
           }
           catch (IOException ex) {
           retMap.put(currentMethod, "false");
           }
           retMap.put(currentMethod, "true");
           */
        return jh.getObjectMapper().writeValueAsString(exMap);
    }

    /*
       @Override
       public String isFeasible(String exchange) throws Exception {
       ExchangeCredentials exchangeCredentials = new ExchangeCredentials(exchange, "bogus", "bogus", "bogus");
       Optional<AccountService> accountService;
       ObjectNode errorMap = jh.getObjectNode();

       try {
       accountService = Optional.ofNullable(xChangeFactory.getAccountService(exchangeCredentials));
       if (!accountService.isPresent()){
       errorMap.put("ERROR", exchangeCredentials.getExchange() + " has no account service");
//return errorMap;
return jh.getObjectMapper().writeValueAsString(errorMap);
       }
       }
       catch (XChangeServiceException ex) {
// import java.time.LocalDateTime;
errorMap.put("ERROR", exchangeCredentials.getExchange() + ex.toString() + ": " + ex.getMessage());
//return errorMap;
return jh.getObjectMapper().writeValueAsString(errorMap);
       }
       catch (IOException ex) {
// import java.time.LocalDateTime;
errorMap.put("ERROR", ex.getMessage());
//return errorMap;
return jh.getObjectMapper().writeValueAsString(errorMap);
       }
       log.debug("balancemap " + balanceMap);
//return balanceMap;
//return jh.getObjectMapper().writeValueAsString(balanceMap);
return "{\"Success\":\"all methods supported}";
       }
       */

    @Override
    public String fillOrKill(List<Order> orders) throws Exception {
        List<OrderResponse> orderResponses = new ArrayList<OrderResponse>();
        OrderResponse thisOrderResponse = null;
        Order thisOrder = null;
        boolean isCanceled = false;
        String response = "";


        //TODO does not fully support more than 2 orders beause it doesn't cancel the
        //first N orders, only the previous one.

        //try {
            for (int i = 0; i < orders.size(); i++) {
                thisOrderResponse = null;
                thisOrder = null;
                try {
                    thisOrder = orders.get(i);
                    thisOrderResponse = placeLimitOrder(thisOrder);

                    if (!(thisOrderResponse.getOrderStatus().hasStatus(PLACED))) {
                        isCanceled = cancelLimitOrder(thisOrder);
                        OrderStatusTypes prevOrderStatusType = thisOrderResponse.getOrderStatus().getOrderStatusType();
                        String prevOrderStatusPhrase =  thisOrderResponse.getOrderStatus().getOrderStatusPhrase();
                        if (isCanceled) {
                            //thisOrderResponse.getOrderStatus().setOrderStatusType(CANCELED);
                            //thisOrderResponse.getOrderStatus().setOrderStatusPhrase("Previously: " + prevOrderStatusType.toString() + ", " + prevOrderStatusPhrase);
                        }
                        else {
                            //thisOrderResponse.getOrderStatus().setOrderStatusType(CANCEL_FAILED);
                            //thisOrderResponse.getOrderStatus().setOrderStatusPhrase("Previously: " + prevOrderStatusType.toString() + ", " + prevOrderStatusPhrase);
                        }
                    }
                }
                catch (Exception e) {
                    OrderResponse.OrderResponseBuilder orderResponseBuilder = OrderResponse.
                        builder();
                    if (thisOrder == null) {
                        orderResponseBuilder
                            .orderType(null)
                            .altfundId(null)
                            .orderStatus(new OrderStatus(PROCESSING_FAILED, e))
                            .orderSpec(null);
                    }
                    else {
                        orderResponseBuilder
                            .orderType(thisOrder.getOrderType())
                            .altfundId(thisOrder.getAltfundId())
                            .orderStatus(new OrderStatus(PROCESSING_FAILED, e))
                            .orderSpec(thisOrder.getOrderSpec());
                    }
                    thisOrderResponse = orderResponseBuilder.build();
                }
                finally {
                    orderResponses.add(thisOrderResponse);
                }
            }
        //}
        //catch (Exception e) {
        //    throw e;
        //}
        response = jh.getObjectMapper().writeValueAsString(orderResponses);
        return response;
    }

    @Override
    public String getOrders(GetOrdersParams params) throws Exception{
        TradeService tradeService = null;
        Collection<org.knowm.xchange.dto.Order> orders = null;
        String response = "";

        try {
            tradeService = xChangeFactory.getTradeService(params.getExchangeCredentials());
            orders = tradeService.getOrder(params.getOrderIds());
            log.debug("get orders: {}", orders);

            //TODO use getExchangeScale()
            //scale = xChangeFactory.getExchangeScale(order.getExchangeCredentials(), currencyPair);
        }
        catch (Exception e){
            log.debug("Error from trade service resulting from calling getOrder: {}", e);
            throw e;
        }
        log.debug("Order Response returning");

        response = jh.getObjectMapper().writeValueAsString(orders);
        return response;
    }

    @Override
    public OrderResponse placeLimitOrder(Order order) throws Exception{
        OrderSpec orderSpec = null;
        ExchangeCredentials exchangeCredentials = null;
        OrderResponse orderResponse = null;
        ObjectNode errorMap = jh.getObjectNode();
        TradeService tradeService = null;
        CurrencyPair currencyPair = null;
        exchangeCredentials = order.getExchangeCredentials();
        String orderType = order.getOrderType();

        if (!("ASK" == orderType.toUpperCase()) || !("BID" == orderType.toUpperCase())) {
            //errorMap.put("ERROR", "order type MUST be equal to 'ASK' or 'BID'");
            //return errorMap;
            log.error("wrong value, must be ASK or BID, was {}, {}", orderType);
            //TODO throw XChangeServiceException;
        }

        try {
            tradeService = xChangeFactory.getTradeService(exchangeCredentials);
            org.knowm.xchange.Exchange exchange = xChangeFactory.getExchange(exchangeCredentials);
            currencyPair = new CurrencyPair(
                    order.getOrderSpec().getBaseCurrency().name(),
                    order.getOrderSpec().getQuoteCurrency().name()
                    );

            //scale = xChangeFactory.getExchangeScale(order.getExchangeCredentials(), currencyPair);

            orderResponse = limitOrderPlacer.placeOrder(order, tradeService, currencyPair, exchange, jh);
            /*
               if (orderResponse.isRetryable()) {
            //TODO is using same orderResponse wrong?
            orderResponse = limitOrderPlacer.placeOrder(order, tradeService, currencyPair, scale, jh);
               }
               */
        }
        /*
           catch (IOException e) {
           log.error("XChangeServiceException {}: " + e, e.getMessage());
           }
           catch (XChangeServiceException e) {
           log.error("XChangeServiceException {}: " + e, e.getMessage());
           }
           catch (NoSuchElementException e) {
           log.error("NoSuchElementException {}: " + e, e.getMessage());
           }
           catch (RuntimeException re) {
           log.error("Non-retyable error {}: " + re, re.getMessage());
           }
           */
        catch (Exception e){
            throw e;
        }
        log.debug("Order Response returning");
        return orderResponse;
    }

    @Override
    public String getTradeHistory(TradeHistory tradeHistory) throws Exception {
        ExchangeCredentials exchangeCredentials = null;
        TradeService tradeService = null;
        CurrencyPair currencyPair = null;
        TradeHistoryParams tradeHistoryParams = null;
        int scale = 5;
        exchangeCredentials = tradeHistory.getExchangeCredentials();
        tradeHistoryParams = tradeHistory.getTradeHistoryParams();
        TradeHistoryParamsAll tradeParams = null;
        UserTrades userTrades = null;
        String response = "";

        log.debug("Begin retrieve trade history");
        try {
            tradeService = xChangeFactory.getTradeService(exchangeCredentials);

            log.debug("Call to Dozer...");
            tradeParams = dozerBeanMapper.map(tradeHistoryParams, TradeHistoryParamsAll.class);
            log.debug("Dozer call success.");

            userTrades = tradeService.getTradeHistory(tradeParams);

            response = jh.getObjectMapper().writeValueAsString(userTrades);

        }
        catch (Exception ex) {
            log.debug("{}: {}", ex.getMessage());
            throw ex;
        }
        return response;
    }

    @Override
    public String getOpenOrders(ExchangeCredentials exchangeCredentials) throws Exception {
        //ExchangeCredentials exchangeCredentials = null;
        //exchangeCredentials = openOrder.getExchangeCredentials();

        org.knowm.xchange.service.trade.params.orders.OpenOrdersParams knowmOpenOrderParms = null;
        //knowmOpenOrderParms = openOrder.getOpenOrderParams();

        List<LimitOrder> openOrders = null;
        ObjectNode errorMap = jh.getObjectNode();
        //ObjectNode userTradesMap = jh.getObjectNode();
        TradeService tradeService = null;
        CurrencyPair currencyPair = null;
        int scale = 5;
        UserTrades userTrades = null;
        String response = "";

        try {
            tradeService = xChangeFactory.getTradeService(exchangeCredentials);
            knowmOpenOrderParms = tradeService.createOpenOrdersParams();

            /*
               CurrencyPair cp = new CurrencyPair(
               openOrder.getOpenOrderParams().getBaseCurrency(),
               openOrder.getOpenOrderParams().getQuoteCurrency()
               );
               */
            //knowmOpenOrderParms.setCurrencyPair(cp);


            /*
               knowmOpenOrderParms = dozerBeanMapper.map(
               openOrder.getOpenOrderParams(),
               org.knowm.xchange.service.trade.params.orders.OpenOrdersParams.class);
               openOrders = tradeService.getOpenOrders(knowmOpenOrderParms).getOpenOrders();
               */

            //openOrders = tradeService.getOpenOrders(knowmOpenOrderParms).getOpenOrders();
            response = tradeService.getOpenOrders(knowmOpenOrderParms).toString();
            log.debug("OPEN ORDERS: {}", response);

            //userTradesMap = ExtractUserTrades.toJson(userTrades, exchangeCredentials.getExchange(), jh);
            //response = jh.getObjectMapper().writeValueAsString(openOrders);

        }
        catch (Exception e) {
            throw e;
        }
        /*
        //TODO return errors as json
        catch (IOException e) {
        log.error("XChangeServiceException {}: " + e, e.getMessage());
        }
        catch (XChangeServiceException e) {
        log.error("XChangeServiceException {}: " + e, e.getMessage());
        }
        catch (RuntimeException re) {
        log.error("Non-retyable error {}: " + re, re.getMessage());
        }
        */
        return response;
    }

    @Override
    public String getAvailableMarkets(List<CurrenciesOnExchange> currenciesOnExchanges) throws Exception {
        String response = "";
        ExchangeMetaData metaData = null;
        Map<CurrencyPair, List<String>> marketByExchanges = new HashMap<CurrencyPair, List<String>>();
        Map<CurrencyPair, CurrencyPairMetaData> cpMetaData = null;
        List<String> currencies = null;
        CurrencyPair cp = null;
        CurrencyPairMetaData cpmd = null;
        List<String> exchanges = null;
        String currExchange = "";
        try {
            log.debug("curr one xchanges size {}.", currenciesOnExchanges.size());
            for (int i = 0; i < currenciesOnExchanges.size(); i++) {
                currExchange = currenciesOnExchanges.get(i).getExchange();
                metaData = xChangeFactory.getExchangeMetaData(currExchange);
                cpMetaData = metaData.getCurrencyPairs();
                currencies = currenciesOnExchanges.get(i).getCurrencies();
                int numCurrencies = currencies.size();
                for (int j = 0; j < numCurrencies; j++) {
                    for (int k = 0; k < numCurrencies -1; k++) {
                        if (j == k) {
                            k += 1;
                            if (k == numCurrencies) {
                                break;
                            }
                        }

                        cp = new CurrencyPair(
                                currencies.get(j),
                                currencies.get(k));

                        cpmd = cpMetaData.get(cp);
                        if (cpmd != null) {
                            exchanges = marketByExchanges.get(cp);
                            if (exchanges == null) {
                                exchanges = new ArrayList<String>();
                                exchanges.add(currenciesOnExchanges.get(i).getExchange());
                                marketByExchanges.put(cp,
                                        exchanges);
                            } else {
                                exchanges.add(currenciesOnExchanges.get(i).getExchange());
                            }
                        }
                    }
                }
                //response = metaData.getCurrencyPairs().toString();
                //marketsByExchange.put(currenciesOnExchanges.get(i).getExchange(), response);
            }
        }
        catch (Exception e) {
            throw e;
        }
        response = jh.getObjectMapper().writeValueAsString(marketByExchanges);
        return response;
    }
}
