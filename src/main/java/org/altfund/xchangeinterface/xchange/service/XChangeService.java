package org.altfund.xchangeinterface.xchange.service;

import org.altfund.xchangeinterface.xchange.model.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Map;

/**
 * altfund
 */
public interface XChangeService {

    Map<String, String> getExchangeCurrencies(String exhange);
    ObjectNode getExchangeBalances(Map<String, String> params);

}
