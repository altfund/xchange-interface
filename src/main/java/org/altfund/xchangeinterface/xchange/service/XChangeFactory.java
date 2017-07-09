package org.altfund.xchangeinterface.xchange.service;

import java.util.Set;
import org.altfund.xchangeinterface.xchange.model.Exchange;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.knowm.xchange.dto.meta.ExchangeMetaData;

/**
 * altfund
 */
public interface XChangeFactory {

  Set<Exchange> getExchanges();

  ExchangeMetaData getExchangeMetaData(String exchangeName) throws XChangeServiceException;

}
