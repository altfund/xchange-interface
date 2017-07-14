package org.altfund.xchangeinterface.xchange.config;

import org.altfund.xchangeinterface.xchange.service.XChangeFactory;
import org.altfund.xchangeinterface.xchange.service.XChangeFactoryImpl;
import org.altfund.xchangeinterface.xchange.service.XChangeService;
import org.altfund.xchangeinterface.xchange.service.XChangeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.altfund.xchangeinterface.restApi.currency.CurrencyController;
import org.altfund.xchangeinterface.restApi.balance.BalanceController;
import org.altfund.xchangeinterface.restApi.test.TestEndPointController;
import org.altfund.xchangeinterface.util.JsonHelper;
import org.altfund.xchangeinterface.util.Pipeline;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * altfund
 */
@Configuration
public class XChangeServiceConfig {

  @Bean
  public XChangeFactory xChangeFactory() {
    return new XChangeFactoryImpl();
  }

  @Bean
  public XChangeService xChangeService(XChangeFactory xChangeFactory, JsonHelper jh) {
    return new XChangeServiceImpl(xChangeFactory, jh);
  }

  @Bean
  public BalanceController balanceController(XChangeService xChangeService, JsonHelper jh) {
    return new BalanceController(xChangeService, jh);
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

  @Bean
  public TestEndPointController testEndPointController(JsonHelper jh) {
    return new TestEndPointController(jh);
  }
}
