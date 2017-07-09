package org.altfund.xchangeinterface.xchange.model;

/**
 * altfund
 */
public enum Exchange {

  GDAX("org.knowm.xchange.gdax.GDAXExchange"),
  KRAKEN("org.knowm.xchange.kraken.KrakenExchange");

  private final String exchangeClassName;

  Exchange(String exchangeClassName) {
    this.exchangeClassName = exchangeClassName;
  }

  public String getExchangeClassName() {
    return exchangeClassName;
  }

}
