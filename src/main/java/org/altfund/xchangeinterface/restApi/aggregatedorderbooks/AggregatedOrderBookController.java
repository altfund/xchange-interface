package org.altfund.xchangeinterface.restApi.aggregateorderbook;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.altfund.xchangeinterface.restApi.util.ResponseHandler;
import org.altfund.xchangeinterface.util.JsonHelper;
import org.altfund.xchangeinterface.xchange.model.EncryptedOrder;
import org.altfund.xchangeinterface.xchange.model.MarketByExchanges;
import org.altfund.xchangeinterface.xchange.service.XChangeService;
import org.altfund.xchangeinterface.xchange.service.MessageEncryption;

/*
 * The above example does not specify GET vs. PUT, POST, and so forth, because
 * @RequestMapping maps all HTTP operations by default. Use
 * @RequestMapping(method=GET) to narrow this mapping.
 */
@RestController
@Slf4j
public class AggregatedOrderBookController {
    private final XChangeService xChangeService;
    private final JsonHelper jh;
    private final ResponseHandler rh;
    private final MessageEncryption messageEncryption;

    public AggregatedOrderBookController(XChangeService xChangeService, JsonHelper jh, ResponseHandler rh, MessageEncryption messageEncryption) {
        this.xChangeService = xChangeService;
        this.jh = jh;
        this.rh = rh;
        this.messageEncryption = messageEncryption;
    }

    @RequestMapping(value = "/aggregateorderbooks", produces = "application/json")
    public ResponseEntity<String> json(@RequestBody Map<String, String> params) {
        MarketByExchanges marketByExchanges = null;
        String response = "";
        EncryptedOrder encryptedOrder;
        try {
            response = jh.getObjectMapper().writeValueAsString(params);
            //log.debug("rec str {}.", response);
            encryptedOrder = jh.getObjectMapper().readValue(response, EncryptedOrder.class);
            //log.debug("rec iv {}.", encryptedOrder.getIv());
            //log.debug("rec data {}.", encryptedOrder.getEncryptedData());

            marketByExchanges = jh.getObjectMapper().readValue(messageEncryption.decrypt(encryptedOrder),
                                                                  MarketByExchanges.class);
            ObjectNode json = xChangeService.getAggregateOrderBooks(marketByExchanges);
            response = jh.getObjectMapper().writeValueAsString(json);
            log.debug("response data {}.", response);

        }
        catch (JsonProcessingException ex) {
            return rh.send(ex, true);
        }
        catch (Exception ex) {
            return rh.send(ex, true);
        }
        //final HttpHeaders httpHeaders= new HttpHeaders();
        //httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        //return new ResponseEntity<String>(response, httpHeaders, HttpStatus.OK);
        return rh.send(response, true);
    }
}
