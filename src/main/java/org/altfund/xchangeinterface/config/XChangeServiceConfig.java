package org.altfund.xchangeinterface.xchange.config;

import org.altfund.xchangeinterface.xchange.service.XChangeFactory;
import org.altfund.xchangeinterface.xchange.service.XChangeFactoryImpl;
import org.altfund.xchangeinterface.xchange.service.XChangeService;
import org.altfund.xchangeinterface.xchange.service.XChangeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.altfund.xchangeinterface.restApi.currency.CurrencyController;
import org.altfund.xchangeinterface.restApi.balance.BalanceController;
import org.altfund.xchangeinterface.util.JsonHelper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.altfund.xchangeinterface.xchange.service.OrderDecryptor;

/**
 * altfund
 */
@Configuration
public class XChangeServiceConfig {

  @Bean
  public OrderDecryptor orderDecryptor() {
    return new OrderDecryptor();
  }

  @Bean
  public XChangeFactory xChangeFactory() {
    return new XChangeFactoryImpl();
  }

  @Bean
  public XChangeService xChangeService(XChangeFactory xChangeFactory, JsonHelper jh) {
    return new XChangeServiceImpl(xChangeFactory, jh);
  }

  @Bean
  public BalanceController balanceController(XChangeService xChangeService, JsonHelper jh, OrderDecryptor orderDecryptor) {
    return new BalanceController(xChangeService, jh, orderDecryptor);
  }

  @Bean
  public CurrencyController currencyController(XChangeService xChangeService) {
    return new CurrencyController(xChangeService);
  }

  @Bean
  public JsonNodeFactory jsonNodeFactory() {
    return new JsonNodeFactory(true);
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  public JsonHelper jsonHelper(JsonNodeFactory jsonNodeFactory, ObjectMapper objectMapper) {
    return new JsonHelper(jsonNodeFactory, objectMapper);
  }

  //@Bean
  //public TestEndPointController testEndPointController(JsonHelper jh) {
  //  return new TestEndPointController(jh);
  //}
}
