package org.altfund.xchangeinterface.xchange.service;

import java.util.Set;
import java.util.Map;
import org.altfund.xchangeinterface.xchange.model.Exchange;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.service.account.AccountService;

/**
 * altfund
 */
public interface XChangeFactory {

  Set<Exchange> getExchanges();

  //TODO the throws should it be there?
  ExchangeMetaData getExchangeMetaData(String exchangeName) throws XChangeServiceException;
  AccountService getAccountService(String exchangeName) throws XChangeServiceException;
  void setProperties(String exchangeName);
  void setProperties(Map<String, String> params);

}
