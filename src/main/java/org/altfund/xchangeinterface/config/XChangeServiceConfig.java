package org.altfund.xchangeinterface.xchange.config;

import org.altfund.xchangeinterface.xchange.service.XChangeFactory;
import org.altfund.xchangeinterface.xchange.service.XChangeFactoryImpl;
import org.altfund.xchangeinterface.xchange.service.XChangeService;
import org.altfund.xchangeinterface.xchange.service.XChangeServiceImpl;
import org.altfund.xchangeinterface.xchange.service.util.LimitOrderPlacer;
import org.altfund.xchangeinterface.restApi.util.ResponseHandler;
import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.altfund.xchangeinterface.restApi.currency.CurrencyController;
import org.altfund.xchangeinterface.api.balance.BalanceEndpoint;
import org.altfund.xchangeinterface.util.JsonHelper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.altfund.xchangeinterface.xchange.service.MessageEncryption;

/**
 * altfund
 */
@Configuration
public class XChangeServiceConfig {


  @Bean
  public LimitOrderPlacer limitOrderPlacer() {
    return new LimitOrderPlacer();
  }

  @Bean
  public DozerBeanMapper dozerBeanMapper() {
    return new DozerBeanMapper();
  }

  @Bean
  public ResponseHandler responseHandler(MessageEncryption me) {
    return new ResponseHandler(me);
  }

  @Bean
  public MessageEncryption messageEncryption(JsonHelper jh) {
    return new MessageEncryption(jh);
  }

  @Bean
  public XChangeFactory xChangeFactory() {
    return new XChangeFactoryImpl();
  }

  @Bean
  public XChangeService xChangeService(XChangeFactory xChangeFactory, JsonHelper jh, LimitOrderPlacer limitOrderPlacer, DozerBeanMapper dozerBeanMapper) {
    return new XChangeServiceImpl(xChangeFactory, jh, limitOrderPlacer, dozerBeanMapper);
  }

  @Bean
  public BalanceEndpoint balanceEndpoint(XChangeService xs, JsonHelper jh, ResponseHandler rh) {
    return new BalanceEndpoint(xs, jh, rh);
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
