package org.altfund.xchangeinterface.restApi.currency;

import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.altfund.xchangeinterface.xchange.service.XChangeService;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.altfund.xchangeinterface.util.JsonHelper;
import org.altfund.xchangeinterface.restApi.util.ResponseHandler;
import org.altfund.xchangeinterface.restApi.util.ResponseHandler;
import org.knowm.xchange.currency.Currency;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/*
 * The above example does not specify GET vs. PUT, POST, and so forth, because
 * @RequestMapping maps all HTTP operations by default. Use
 * @RequestMapping(method=GET) to narrow this mapping.
 */

@Slf4j
@RestController
public class CurrencyController {

    private final XChangeService xChangeService;
    private final ResponseHandler rh;
    private final JsonHelper jh;

    public CurrencyController(XChangeService xChangeService, JsonHelper jh, ResponseHandler rh) {
        this.xChangeService = xChangeService;
        this.jh = jh;
        this.rh = rh;
    }
    @RequestMapping(value = "/currency", produces = "application/json")
    public ResponseEntity<String> currencyMap(@RequestParam(value="exchange") String exchange) {
        ObjectNode json = xChangeService.getExchangeCurrencies(exchange);
        String response = "";
        try {
            response = jh.getObjectMapper().writeValueAsString(json);
        } catch (JsonProcessingException ex) {
            response = "{\"ERROR\":\"JsonProcessingException:"+ ex.getMessage() + "\"}";
        }
        return rh.send(response, true);
    }
}

