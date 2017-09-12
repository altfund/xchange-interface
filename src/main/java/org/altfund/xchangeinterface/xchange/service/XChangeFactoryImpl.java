package org.altfund.xchangeinterface.xchange.service;

import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

import org.altfund.xchangeinterface.xchange.model.ExchangeCredentials;
import org.altfund.xchangeinterface.xchange.model.Exchange;



import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;

import java.io.IOException;
import org.knowm.xchange.exceptions.ExchangeException;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;


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
    public int getExchangeScale(ExchangeCredentials exchangeCredentials, CurrencyPair cp) {
        Optional<org.knowm.xchange.Exchange> exchange =
            Optional.ofNullable(exchangeCredsMap.get(exchangeCredentials));

        Optional<ExchangeMetaData> exchangeMetaData =
            Optional.ofNullable(exchange.get().getExchangeMetaData());

        Optional<CurrencyPairMetaData> exCurPairMetaData =
            Optional.ofNullable(exchangeMetaData.get().getCurrencyPairs().get(cp));
        //if (exchangeMetaData.isPresent())
        //    log.debug("exchange meta data {}.", exchangeMetaData.get());

        Optional<CurrencyMetaData> exCurMd =
            Optional.ofNullable(exchangeMetaData.get().getCurrencies().get(cp.base));

        Optional<Integer> exBaseCurScale =
            Optional.ofNullable(exCurMd.get().getScale());

        if (exCurPairMetaData.isPresent()) {
            log.debug("exchange cur pairs {}.", exCurPairMetaData.get());

            if (exCurPairMetaData.get() != null) {
                if (exCurPairMetaData.get().getMinimumAmount() != null) {
                    //if (buyTradableAmount.compareTo(exCurPairMetaData.get().getMinimumAmount()) < 0)
                    //    log.error("Tradeable amount to buy is less than minimum amount supported by exchange {}", orderSpec.getSellExchange());
                     //TODO throq eroor instead of log.
                    return exCurPairMetaData.get().getMinimumAmount().scale();
                } else if (exBaseCurScale.isPresent()){
                    return exBaseCurScale.get();
                } else {
                    return 5;
                }
            }
        }
        return 5;
    }

    @Override
    public void testCurPairMetaData(Map<String, String> params, org.knowm.xchange.dto.Order order) {
        log.debug("test cur pair meta data");
        Optional<org.knowm.xchange.Exchange> exchangeFirstTry =
            Optional.ofNullable(exchangeMap.get(params));
        if (exchangeFirstTry.isPresent()) {
            log.debug("exchange {}  present for given creds.", params.get("exchange"));
            ExchangeMetaData exMetaData = exchangeFirstTry.get().getExchangeMetaData();
            CurrencyPairMetaData curPairMetaData = exMetaData.getCurrencyPairs().get(order.getCurrencyPair());
            if (curPairMetaData == null) {
                log.debug("curPairMetaData is null");
            } else {
                log.debug("max maount {}.", curPairMetaData.getMaximumAmount() );
                log.debug(" min amount {}.", curPairMetaData.getMinimumAmount() );
                log.debug(" price scale {}.", curPairMetaData.getPriceScale() );
                log.debug(" tradingfee {}.", curPairMetaData.getTradingFee() );
                log.debug("tostring {}.", curPairMetaData.toString() );
            }
        }
        else {
            log.debug("exchange {}  NOT FOUND.", params.get("exchange"));
        }

    }

    protected ExchangeSpecification createExchangeSpecification(Exchange exchange, Map<String, String> params) {
        String exchangeClassName = exchange.getExchangeClassName();
        ExchangeSpecification exchangeSpecification = new ExchangeSpecification(exchangeClassName);

        //TODO optional API_KEY/SECRET_KEY?
        //setExchangeProperty(exchange, "USERNAME", exchangeSpecification::setUserName, params.get("username"));
        //setExchangeProperty(exchange, "PASSWORD", exchangeSpecification::setPassword,  params.get("password"));
        setExchangeProperty(exchange, "API_KEY", exchangeSpecification::setApiKey, params.get("key"));
        setExchangeProperty(exchange, "SECRET_KEY", exchangeSpecification::setSecretKey, params.get("secret"));
        if (params.get("passphrase") != null)
            exchangeSpecification.setExchangeSpecificParametersItem("passphrase", params.get("passphrase"));
        else
            exchangeSpecification.setExchangeSpecificParametersItem("passphrase","");

        //exchangeSpecification.setApiKey(apiKey);
        //exchangeSpecification.setSecretKey(apiSecret);

        //Map<String, Object> esParams = exchangeSpecification.getExchangeSpecificParameters();

        //for (Map.Entry<String, Object> entry : esParams.entrySet()) {
        //    log.debug("exchange specific param " + entry.getKey());
        //    log.debug("exchange specific object " + entry.getValue().toString());
        //    exchangeSpecification.setExchangeSpecificParametersItem(entry.getKey(), params.get(entry.getKey()));
        //}
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

    /*
       protected void setExchangeProperty(
       Exchange exchange, String propertyName, Consumer<String> propertyConsumer) {

       String exchangePropertyName = (exchange.name() + "_" + propertyName).toUpperCase();

       Optional<String> exchangePropertyValue = Optional.ofNullable(environment.getProperty(exchangePropertyName));
       if (exchangePropertyValue.isPresent()) {
       log.debug("Setting exchange property {}={}.", exchangePropertyName, exchangePropertyValue.get());
       propertyConsumer.accept(exchangePropertyValue.get());
       }
       else {

       */
    @Override
    public TradeService getTradeService(ExchangeCredentials exchangeCredentials) throws XChangeServiceException, IOException {
        return variableDispatch(XChangeDispatcher.TradeServiceType, exchangeCredentials);
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
           return null;
           */
    }

    //@Override
    private boolean setProperties(String exchangeName) {

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

    //@Override
    private boolean setProperties(ExchangeCredentials exchangeCredentials) {

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

}
