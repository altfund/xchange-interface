package org.altfund.xchangeinterface.xchange.model;

/**
 * altfund
 */
public enum Exchange {

  GDAX("org.knowm.xchange.gdax.GDAXExchange"),
  KRAKEN("org.knowm.xchange.kraken.KrakenExchange"),
  BITFINEX("org.knowm.xchange.bitfinex.v1.BitfinexExchange"),
  POLONIEX("org.knowm.xchange.poloniex.PoloniexExchange");

  private final String exchangeClassName;

  Exchange(String exchangeClassName) {
    this.exchangeClassName = exchangeClassName;
  }

  public String getExchangeClassName() {
    return exchangeClassName;
  }

}
