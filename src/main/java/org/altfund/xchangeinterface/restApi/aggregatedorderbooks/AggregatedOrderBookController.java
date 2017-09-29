package org.altfund.xchangeinterface.restApi.aggregateorderbook;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.altfund.xchangeinterface.xchange.service.XChangeService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.altfund.xchangeinterface.util.JsonHelper;
import org.altfund.xchangeinterface.restApi.util.ResponseHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.altfund.xchangeinterface.xchange.model.MarketByExchanges;

//TODO remove
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
/*
 * The above example does not specify GET vs. PUT, POST, and so forth, because
 * @RequestMapping maps all HTTP operations by default. Use
 * @RequestMapping(method=GET) to narrow this mapping.
 */
@RestController
public class AggregatedOrderBookController {
    private final XChangeService xChangeService;
    private final JsonHelper jh;
    private final ResponseHandler rh;

    public AggregatedOrderBookController(XChangeService xChangeService, JsonHelper jh, ResponseHandler rh) {
        this.xChangeService = xChangeService;
        this.jh = jh;
        this.rh = rh;
    }

    @RequestMapping(value = "/aggregateorderbooks", produces = "application/json")
    public ResponseEntity<String> json(@RequestBody MarketByExchanges marketByExchanges) {
        ObjectNode json = xChangeService.getAggregateOrderBooks(marketByExchanges);
        String response = "";
        try {
            response = jh.getObjectMapper().writeValueAsString(json);
        } catch (JsonProcessingException ex) {
            response = "{\"ERROR\":\"JsonProcessingException:"+ ex.getMessage() + "\"}";
        }
        final HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(response, httpHeaders, HttpStatus.OK);
        //return rh.send(response, true);
    }
}
