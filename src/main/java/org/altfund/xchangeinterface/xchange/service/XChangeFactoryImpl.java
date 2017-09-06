package org.altfund.xchangeinterface.xchange.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.altfund.xchangeinterface.xchange.model.ExchangeCredentials;
import org.altfund.xchangeinterface.xchange.model.Exchange;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import java.io.IOException;

/**
 * altfund
 */
@Slf4j
public class XChangeFactoryImpl implements XChangeFactory {

    private Map<String, org.knowm.xchange.Exchange> exchangeMap = new LinkedHashMap<>();
    private Map<ExchangeCredentials, org.knowm.xchange.Exchange> exchangeCredsMap = new LinkedHashMap<>();

    private <T> T variableDispatch(XChangeDispatcher dispatcher, String exchangeName) throws XChangeServiceException, IOException{
        // variable dispatch will emulate getTradeService but call dispatcher.comeback(exchange) instead.
        // In order to handle not re-initializing exchanges all the time, it would be preferable
        // to see how "apply specification" works it could be the case that it is better,
        // and we only call remoteInit on an exchange when we get an error in order to
        // see if a re-initialization would help. Either way, dispatcher.comeback(exchange)
        // as a line is invaluable to genearlizing the cache-ing strategy. The avoidance
        // or re-initialization for every exchange <=> credential pair would be highly desirable.
        Optional<org.knowm.xchange.Exchange> exchangeFirstTry =
            Optional.ofNullable(exchangeMap.get(exchangeName));

        if (!exchangeFirstTry.isPresent()) {
            if (setProperties(exchangeName)) {
                Optional<org.knowm.xchange.Exchange> exchangeSecondTry =
                    Optional.ofNullable(exchangeMap.get(exchangeName));

                if (!exchangeSecondTry.isPresent()) {
                    throw new XChangeServiceException("Unknown exchange on second try: " + exchangeName);
                }
                else {
                    log.debug("Adding new exchange {}, calling remote init manually.", exchangeName);
                    try {
                        exchangeSecondTry.get().remoteInit();
                    }
                    catch(IOException ex) {
                        log.debug("IO ex on remoteInit {}.", ex.getMessage());
                        throw ex;
                    }
                    catch(ExchangeException ex) {
                        log.debug("exchange exception ex on remoteInit {}.", ex.getMessage());
                        throw ex;
                    }
                    return dispatcher.comeback(exchangeSecondTry.get());
                }
            } else {
                throw new XChangeServiceException("Unknown exchange, couldn't set properties with params: " + exchangeName);
            }
        }
        else {
            log.debug("exchange {} already present for given creds.", exchangeName);
            return dispatcher.comeback(exchangeFirstTry.get());
        }
    }

    private <T> T variableDispatch(XChangeDispatcher dispatcher, ExchangeCredentials exchangeCredentials) throws XChangeServiceException, IOException{
        // variable dispatch will emulate getTradeService but call dispatcher.comeback(exchange) instead.
        // In order to handle not re-initializing exchanges all the time, it would be preferable
        // to see how "apply specification" works it could be the case that it is better,
        // and we only call remoteInit on an exchange when we get an error in order to
        // see if a re-initialization would help. Either way, dispatcher.comeback(exchange)
        // as a line is invaluable to genearlizing the cache-ing strategy. The avoidance
        // or re-initialization for every exchange <=> credential pair would be highly desirable.
        Optional<org.knowm.xchange.Exchange> exchangeFirstTry =
            Optional.ofNullable(exchangeCredsMap.get(exchangeCredentials));

        if (!exchangeFirstTry.isPresent()) {
            if (setProperties(exchangeCredentials)) {
                Optional<org.knowm.xchange.Exchange> exchangeSecondTry =
                    Optional.ofNullable(exchangeCredsMap.get(exchangeCredentials));

                if (!exchangeSecondTry.isPresent()) {
                    throw new XChangeServiceException("Unknown exchange on second try: " + exchangeCredentials.getExchange());
                }
                else {
                    log.debug("Adding new exchange {}, calling remote init manually.", exchangeCredentials.getExchange());
                    try {
                        exchangeSecondTry.get().remoteInit();
                    }
                    catch(IOException ex) {
                        log.debug("IO ex on remoteInit {}.", ex.getMessage());
                        throw ex;
                    }
                    catch(ExchangeException ex) {
                        log.debug("exchange exception ex on remoteInit {}.", ex.getMessage());
                        throw ex;
                    }
                    return dispatcher.comeback(exchangeSecondTry.get());
                }
            } else {
                throw new XChangeServiceException("Unknown exchange, couldn't set properties with params: " + exchangeCredentials.getExchange());
            }
        }
        else {
            log.debug("exchange {} already present for given creds.", exchangeCredentials.getExchange());
            return dispatcher.comeback(exchangeFirstTry.get());
        }
    }


    @Override
    public AccountService getAccountService(ExchangeCredentials exchangeCredentials) throws XChangeServiceException, IOException {
        return variableDispatch(XChangeDispatcher.AccountServiceType, exchangeCredentials);
    }

    @Override
    public ExchangeMetaData getExchangeMetaData(String exchangeName) throws XChangeServiceException, IOException{
        return variableDispatch(XChangeDispatcher.ExchangeCurrencyType, exchangeName);
    }

    @Override
    public List<CurrencyPair> getExchangeSymbols(String exchangeName) throws XChangeServiceException, IOException{
        return variableDispatch(XChangeDispatcher.ExchangeSymbolsType, exchangeName);
    }

    @Override
    public MarketDataService getMarketDataService(String exchangeName) throws XChangeServiceException, IOException {
        return variableDispatch(XChangeDispatcher.MarketDataServiceType, exchangeName);
        /*
           for (Map.Entry<Exchange, org.knowm.xchange.Exchange> entry : exchangeMap.entrySet()) {
           if (entry.getKey().getExchangeClassName().contains(exchangeName)) {
           return variableDispatch(XChangeDispatcher.MarketDataServiceType, entry.getValue(), exchangeName);
           }
           }
           return null;
           */
    }

    @Override
    public boolean setProperties(String exchangeName) {

        log.debug("requested  " + exchangeName);
        for (Exchange exchange : Exchange.values()) {
            if (exchange.getExchangeClassName().contains(exchangeName)) {
                ExchangeSpecification exchangeSpecification = createExchangeSpecification(exchange);
                try {
                    exchangeMap.put(exchangeName, ExchangeFactory.INSTANCE.createExchange(exchangeSpecification));
                    log.debug("Added exchange " + exchangeName);
                    return true;
                } catch (ExchangeException ee) {
                    log.error("Couldn't create XChange " + exchange, ee);
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean setProperties(ExchangeCredentials exchangeCredentials) {

        log.debug("\nGiven Parameters for exchange: " + exchangeCredentials.getExchange());
        for (Exchange exchange : Exchange.values()) {
            if (exchange.getExchangeClassName().contains(exchangeCredentials.getExchange())) {

                Optional<org.knowm.xchange.Exchange> exchangeTry =
                    Optional.ofNullable(exchangeMap.get(exchangeCredentials.getExchange()));

                try {
                    ExchangeSpecification exchangeSpecification = createExchangeSpecification(exchange, exchangeCredentials);
                    if (exchangeTry.isPresent()) {
                        exchangeTry.get().applySpecification(exchangeSpecification);
                        exchangeCredsMap.put(exchangeCredentials, exchangeTry.get());
                        log.debug("apply specification " + exchange);
                    }
                    else {
                        exchangeCredsMap.put(exchangeCredentials, ExchangeFactory.INSTANCE.createExchange(exchangeSpecification));
                        log.debug("Added exchange " + exchange);
                    }
                    return true;
                } catch (ExchangeException ee) {
                    log.error("Couldn't create XChange " + exchange, ee);
                    return false;
                }
            }
        }
        return false;
    }

    protected ExchangeSpecification createExchangeSpecification(Exchange exchange) {
        String exchangeClassName = exchange.getExchangeClassName();
        ExchangeSpecification exchangeSpecification = new ExchangeSpecification(exchangeClassName);

        return exchangeSpecification;
    }

    protected ExchangeSpecification createExchangeSpecification(Exchange exchange, ExchangeCredentials exchangeCredentials) {
        String exchangeClassName = exchange.getExchangeClassName();
        ExchangeSpecification exchangeSpecification = new ExchangeSpecification(exchangeClassName);

        setExchangeProperty(exchange, "API_KEY", exchangeSpecification::setApiKey, exchangeCredentials.getKey());
        setExchangeProperty(exchange, "SECRET_KEY", exchangeSpecification::setSecretKey, exchangeCredentials.getSecret());
        if (exchangeCredentials.getPassphrase() != null || exchangeCredentials.getPassphrase() != "")
            exchangeSpecification.setExchangeSpecificParametersItem("passphrase", exchangeCredentials.getPassphrase());
        else
            exchangeSpecification.setExchangeSpecificParametersItem("passphrase","");

        return exchangeSpecification;
    }

    protected void setExchangeProperty(
            Exchange exchange, String propertyName, Consumer<String> propertyConsumer, String property) {
        log.debug("props for exchange " + exchange.name());
        String exchangePropertyName = (exchange.name() + "_" + propertyName).toUpperCase();
        log.debug("props " + exchangePropertyName);

        String exchangePropertyValue = property;
        if (exchangePropertyValue != "") {
            log.debug("Setting exchange property {}", property);
            propertyConsumer.accept(exchangePropertyValue);
        }
            }
}
