package org.altfund.xchangeinterface.xchange.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EncryptedOrder {

  @JsonProperty("iv")
  private String iv;

  @JsonProperty("encrypted_data")
  private String encryptedData;
}
