package org.altfund.xchangeinterface.xchange.service;

import java.util.Set;
import java.util.Map;
import java.util.List;
import org.altfund.xchangeinterface.xchange.model.Exchange;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import java.io.IOException;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.currency.CurrencyPair;
import org.altfund.xchangeinterface.xchange.model.ExchangeCredentials;

/**
 * altfund
 */
public interface XChangeFactory {

  ExchangeMetaData getExchangeMetaData(String exchangeName) throws XChangeServiceException, IOException;
  AccountService getAccountService(ExchangeCredentials exchangeCredentials) throws XChangeServiceException, IOException;
  List<CurrencyPair> getExchangeSymbols(String exchangeName) throws XChangeServiceException, IOException;
  MarketDataService getMarketDataService(String exchangeName) throws XChangeServiceException, IOException;
  boolean setProperties(String exchangeName);
  boolean setProperties(ExchangeCredentials exchangeCredentials);
}
