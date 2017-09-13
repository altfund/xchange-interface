package org.altfund.xchangeinterface.xchange.model;

import org.altfund.xchangeinterface.xchange.model.ExchangeCredentials;
import org.altfund.xchangeinterface.xchange.model.TradeHistoryParams;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TradeHistory {

  @JsonProperty("trade_params")
  private TradeHistoryParams tradeHistoryParams;

  @JsonProperty("exchange_credentials")
  private ExchangeCredentials exchangeCredentials;

  public TradeHistory(TradeHistoryParams tradeHistoryParams, ExchangeCredentials exchangeCredentials) {
    this.tradeHistoryParams = tradeHistoryParams;
    this.exchangeCredentials = exchangeCredentials;
  }
}
