package org.altfund.xchangeinterface.xchange.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExchangeCredentials {

  @JsonProperty("exchange")
  private String exchange;

  @JsonProperty("key")
  private String key;

  @JsonProperty("secret")
  private String secret;

  @JsonProperty("passphrase")
  private String passphrase;

}
