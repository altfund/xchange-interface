package org.altfund.xchangeinterface.xchange.service;

import java.util.Set;
import java.util.Map;
import java.util.List;
import org.altfund.xchangeinterface.xchange.model.Exchange;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.currency.CurrencyPair;

/**
 * altfund
 */
public interface XChangeFactory {

  Set<Exchange> getExchanges();

  //TODO the throws should it be there?
  ExchangeMetaData getExchangeMetaData(String exchangeName) throws XChangeServiceException;
  AccountService getAccountService(String exchangeName) throws XChangeServiceException;
  List<CurrencyPair> getExchangeSymbols(String exchangeName);
  MarketDataService getMarketDataService(String exchangeName);
  void setProperties(String exchangeName);
  void setProperties(Map<String, String> params);

}
