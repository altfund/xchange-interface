package org.altfund.xchangeinterface.xchange.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.altfund.xchangeinterface.xchange.model.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.springframework.beans.factory.InitializingBean;
//import org.springframework.context.EnvironmentAware;
//import org.springframework.core.env.Environment;

/**
 * altfund
 */
@Slf4j
public class XChangeFactoryImpl implements XChangeFactory { // EnvironmentAware, InitializingBean, XChangeFactory {

    //private Environment environment;
    //TODO this is the wrong DS for this job know that this is set on a per
    //req basis
    private Map<Exchange, org.knowm.xchange.Exchange> exchangeMap;

    @Override
    public void setProperties(String exchangeName) {
        Map<Exchange, org.knowm.xchange.Exchange> exchangeMap = new LinkedHashMap<>();

        for (Exchange exchange : Exchange.values()) {
            log.info("For exchange " + exchange.getExchangeClassName());
            log.info("requested  " + exchangeName);
            if (exchange.getExchangeClassName().contains(exchangeName)) {
                ExchangeSpecification exchangeSpecification = createExchangeSpecification(exchange);
                try {
                    exchangeMap.put(exchange, ExchangeFactory.INSTANCE.createExchange(exchangeSpecification));
                    log.info("Added exchange " + exchange);
                } catch (ExchangeException ee) {
                    //TODO NEEDS TO BE CAUGHT AND REPORTED TO CONSUMER
                    log.error("Couldn't create XChange " + exchange, ee);
                }
            }
        }
        this.exchangeMap = exchangeMap;
    }

    @Override
    public void setProperties(Map<String, String> params) {
        Map<Exchange, org.knowm.xchange.Exchange> exchangeMap = new LinkedHashMap<>();

        log.info("\nGiven Parameters for exchange: " + params.get("exchange"));
        for (Map.Entry<String, String> entry : params.entrySet()) {
            log.info("property: " + entry.getKey());
            log.info("value: " + entry.getValue());
        }

        for (Exchange exchange : Exchange.values()) {
            if (exchange.getExchangeClassName().contains(params.get("exchange"))) {
                ExchangeSpecification exchangeSpecification = createExchangeSpecification(exchange, params);
                try {
                    exchangeMap.put(exchange, ExchangeFactory.INSTANCE.createExchange(exchangeSpecification));
                    log.info("Added exchange " + exchange);
                } catch (ExchangeException ee) {
                    //TODO NEEDS TO BE CAUGHT AND REPORTED TO CONSUMER
                    log.error("Couldn't create XChange " + exchange, ee);
                }
            }
        }
        this.exchangeMap = Collections.unmodifiableMap(exchangeMap);
    }

    @Override
    public Set<Exchange> getExchanges() {
        return exchangeMap.keySet();
    }

    @Override
    public AccountService getAccountService(String exchangeName) {
        for (Map.Entry<Exchange, org.knowm.xchange.Exchange> entry : exchangeMap.entrySet()) {
            if (entry.getKey().getExchangeClassName().contains(exchangeName)) {
                return entry.getValue().getAccountService();
            }
        }
        return null;
    }

    @Override
    public ExchangeMetaData getExchangeMetaData(String exchangeName) {
        for (Map.Entry<Exchange, org.knowm.xchange.Exchange> entry : exchangeMap.entrySet()) {
            if (entry.getKey().getExchangeClassName().contains(exchangeName)) {
                return entry.getValue().getExchangeMetaData();
            }
        }
        return null;
    }

    //@Override
    //public void setEnvironment(Environment environment) {
    //    this.environment = environment;
    //}

    protected ExchangeSpecification createExchangeSpecification(Exchange exchange) {
        String exchangeClassName = exchange.getExchangeClassName();
        ExchangeSpecification exchangeSpecification = new ExchangeSpecification(exchangeClassName);

        return exchangeSpecification;
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
        log.info("props for exchange " + exchange.name());
        String exchangePropertyName = (exchange.name() + "_" + propertyName).toUpperCase();
        log.info("props " + exchangePropertyName);

        //Optional<String> exchangePropertyValue = Optional.ofNullable(environment.getProperty(exchangePropertyName));
        //TODO what?
        //Optional<String> exchangePropertyValue = Optional.ofNullable(property);
        String exchangePropertyValue = property;
        if (exchangePropertyValue != "") {
            log.debug("Setting exchange property {}", property);
            propertyConsumer.accept(exchangePropertyValue);
        }
    }
}
