package org.altfund.xchangeinterface.api.balance;

import java.util.Map;
import org.altfund.xchangeinterface.xchange.model.EncryptedOrder;
import org.altfund.xchangeinterface.xchange.model.ExchangeCredentials;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.altfund.xchangeinterface.xchange.service.XChangeService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.altfund.xchangeinterface.util.JsonHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.altfund.xchangeinterface.xchange.service.MessageEncryption;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;

import lombok.extern.slf4j.Slf4j;
import org.altfund.xchangeinterface.restApi.util.ResponseHandler;

/*
 * The above example does not specify GET vs. PUT, POST, and so forth, because
 * @RequestMapping maps all HTTP operations by default. Use
 * @RequestMapping(method=GET) to narrow this mapping.
 */
@Slf4j
public class BalanceEndpoint {
    private final XChangeService xChangeService;
    private final JsonHelper jh;
    private final ResponseHandler rh;
    private ExchangeCredentials exchangeCredentials;

    public BalanceEndpoint(XChangeService xChangeService, JsonHelper jh, ResponseHandler rh) {
        this.xChangeService = xChangeService;
        this.jh = jh;
        this.rh = rh;
    }

    public String getBalance(String jsonExchangeCredentials) {
        String response = "";
        try {
            //encrypted order needs to decrypt to a Map<String, String> :(
            //probs change that to a pojo like in ee.
            exchangeCredentials = jh.getObjectMapper().readValue( jsonExchangeCredentials,
                                                                  ExchangeCredentials.class);
            ObjectNode json = xChangeService.getExchangeBalances(exchangeCredentials);
            response = jh.getObjectMapper().writeValueAsString(json);
        }
        catch (Exception ex) {
            return rh.send(ex);
        }
        return response;
    }
}
