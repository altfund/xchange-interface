package org.altfund.xchangeinterface.restApi.orderbook;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.altfund.xchangeinterface.xchange.service.XChangeService;
import org.altfund.xchangeinterface.xchange.model.EncryptedOrder;
import org.altfund.xchangeinterface.xchange.service.MessageEncryption;
import org.altfund.xchangeinterface.util.JsonHelper;
import org.altfund.xchangeinterface.restApi.util.ResponseHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectReader;

/*
 * The above example does not specify GET vs. PUT, POST, and so forth, because
 * @RequestMapping maps all HTTP operations by default. Use
 * @RequestMapping(method=GET) to narrow this mapping.
 */
@RestController
@Slf4j
public class OrderBookController {
    private final XChangeService xChangeService;
    private final JsonHelper jh;
    private final ResponseHandler rh;
    private EncryptedOrder encryptedOrder;
    private final MessageEncryption messageEncryption;

    public OrderBookController(XChangeService xChangeService, JsonHelper jh, ResponseHandler rh,  MessageEncryption messageEncryption) {
        this.xChangeService = xChangeService;
        this.jh = jh;
        this.messageEncryption = messageEncryption;
        this.rh = rh;
    }

    @RequestMapping(value = "/orderbook", produces = "application/json")
    public ResponseEntity<String> orderbook(@RequestParam Map<String, String> params) {
        log.debug("maybe {}", params);

        //ObjectNode json = xChangeService.getOrderBooks(params);
        String response = "";
        Map<String, String> args = null;
        ObjectNode json = null;
        ObjectReader reader = null;
        try {
            response = jh.getObjectMapper().writeValueAsString(params);
            log.debug("rec str {}.", response);
            encryptedOrder = jh.getObjectMapper().readValue(response, EncryptedOrder.class);
            log.debug("rec iv {}.", encryptedOrder.getIv());
            log.debug("rec data {}.", encryptedOrder.getEncryptedData());
            //args = jh.getObjectMapper().readValue( messageEncryption.decrypt(encryptedOrder),
            //                                                      Map<String, String>.class);
            reader = jh.getObjectMapper().readerFor(Map.class);

            args = reader.readValue(messageEncryption.decrypt(encryptedOrder));

            json = xChangeService.getOrderBooks(args);
            response = jh.getObjectMapper().writeValueAsString(json);
            log.debug(" orderbook: {}", json);

        }
        catch (JsonProcessingException ex) {
            //response = "{\"ERROR\":\"JsonProcessingException:"+ ex.getMessage() + "\"}";
            rh.send(ex, true);
        }
        catch (Exception ex) {
            return rh.send(ex, true);
        }
        return rh.send(response, true);
    }
}
