package org.altfund.xchangeinterface.xchange.config;

import org.altfund.xchangeinterface.xchange.service.XChangeFactory;
import org.altfund.xchangeinterface.xchange.service.XChangeFactoryImpl;
import org.altfund.xchangeinterface.xchange.service.XChangeService;
import org.altfund.xchangeinterface.xchange.service.XChangeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.altfund.xchangeinterface.restApi.currency.CurrencyController;

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
  public XChangeService xChangeService(XChangeFactory xChangeFactory) {
    return new XChangeServiceImpl(xChangeFactory);
  }

  @Bean
  public CurrencyController currencyController(XChangeService XChangeService) {
    return new CurrencyController(XChangeService);
  }

}
