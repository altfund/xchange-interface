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
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * altfund
 */
@Slf4j
public class XChangeFactoryImpl implements EnvironmentAware, InitializingBean, XChangeFactory {

    private Environment environment;
    private Map<Exchange, org.knowm.xchange.Exchange> exchangeMap;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<Exchange, org.knowm.xchange.Exchange> exchangeMap = new LinkedHashMap<>();

        for (Exchange exchange : Exchange.values()) {
            ExchangeSpecification exchangeSpecification = createExchangeSpecification(exchange);
            try {
                exchangeMap.put(exchange, ExchangeFactory.INSTANCE.createExchange(exchangeSpecification));
                log.info("Added exchange " + exchange);
            } catch (ExchangeException ee) {
                //TODO NEEDS TO BE CAUGHT AND REPORTED TO CONSUMER
                log.error("Couldn't create XChange " + exchange, ee);
            }
        }

        this.exchangeMap = Collections.unmodifiableMap(exchangeMap);
    }

    @Override
    public Set<Exchange> getExchanges() {
        return exchangeMap.keySet();
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
            //Optional<org.knowm.xchange.Exchange> exchange =
            //Optional.ofNullable(exchangeMap.get(exchangeName));
        //org.knowm.xchange.Exchange exchange2 = exchangeMap.get(exchangeName);

        //if (!exchange.isPresent()) {
        //    throw new XChangeServiceException("Unknown exchange: " + exchangeName);
        //}

        //return exchange.get().getExchangeMetaData();

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    protected ExchangeSpecification createExchangeSpecification(Exchange exchange) {
        String exchangeClassName = exchange.getExchangeClassName();
        ExchangeSpecification exchangeSpecification = new ExchangeSpecification(exchangeClassName);

        //TODO optional API_KEY/SECRET_KEY?
        setExchangeProperty(exchange, "USERNAME", exchangeSpecification::setUserName);
        setExchangeProperty(exchange, "PASSWORD", exchangeSpecification::setPassword);
        setExchangeProperty(exchange, "API_KEY", exchangeSpecification::setApiKey);
        setExchangeProperty(exchange, "SECRET_KEY", exchangeSpecification::setSecretKey);

        return exchangeSpecification;
    }

    protected void setExchangeProperty(
            Exchange exchange, String propertyName, Consumer<String> propertyConsumer) {

        String exchangePropertyName = (exchange.name() + "_" + propertyName).toUpperCase();

        Optional<String> exchangePropertyValue = Optional.ofNullable(environment.getProperty(exchangePropertyName));
        if (exchangePropertyValue.isPresent()) {
            log.debug("Setting exchange property {}.", exchangePropertyName);
            propertyConsumer.accept(exchangePropertyValue.get());
        }
            }

}
