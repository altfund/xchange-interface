package org.altfund.xchangeinterface.xchange.service;

import java.util.Set;
import java.util.Map;
import java.util.List;

import org.altfund.xchangeinterface.xchange.model.Exchange;
import org.altfund.xchangeinterface.xchange.model.ExchangeCredentials;

import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;

import java.io.IOException;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.knowm.xchange.exceptions.ExchangeException;


/**
 * altfund
 */
public interface XChangeFactory {

  ExchangeMetaData getExchangeMetaData(String exchangeName) throws XChangeServiceException, IOException;
  AccountService getAccountService(ExchangeCredentials exchangeCredentials) throws XChangeServiceException, IOException;
  List<CurrencyPair> getExchangeSymbols(String exchangeName) throws XChangeServiceException, IOException;
  int getExchangeScale(ExchangeCredentials exchangeCredentials, CurrencyPair cp);
  MarketDataService getMarketDataService(String exchangeName) throws XChangeServiceException, IOException;
  //boolean setProperties(String exchangeName);
  //boolean setProperties(ExchangeCredentials exchangeCredentials);
  void testCurPairMetaData(Map<String, String> params, org.knowm.xchange.dto.Order order);
 TradeService getTradeService(ExchangeCredentials exchangeCredentials) throws XChangeServiceException , ExchangeException, IOException;
}
