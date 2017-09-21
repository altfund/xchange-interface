package org.altfund.xchangeinterface.xchange.service;

import org.altfund.xchangeinterface.xchange.model.Order;
import org.altfund.xchangeinterface.xchange.model.ExchangeCredentials;
import org.altfund.xchangeinterface.xchange.model.Exchange;
import org.altfund.xchangeinterface.xchange.model.OrderResponse;
import org.altfund.xchangeinterface.xchange.model.TradeHistory;
import org.altfund.xchangeinterface.xchange.model.OpenOrder;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.meta.CurrencyMetaData;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Map;

/**
 * altfund
 */
public interface XChangeService {

    ObjectNode getExchangeCurrencies(String exhange);
    ObjectNode getTickers(String exchange);
    ObjectNode getOrderBooks(Map<String, String> params);
    ObjectNode getExchangeTradeFees(Map<String, String> params);
    ObjectNode getExchangeBalances(ExchangeCredentials params);
    OrderResponse placeLimitOrder(Order order) throws Exception;
    boolean cancelLimitOrder(Order order) throws Exception;
    String getTradeHistory(TradeHistory tradeHistory) throws Exception;
    String getOpenOrders(ExchangeCredentials exchangeCredentials) throws Exception;
}
