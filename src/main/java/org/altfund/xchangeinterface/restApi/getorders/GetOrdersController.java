package org.altfund.xchangeinterface.restApi.getorders;

import java.util.Map;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.altfund.xchangeinterface.util.JsonHelper;
import org.altfund.xchangeinterface.xchange.model.EncryptedOrder;
import org.altfund.xchangeinterface.xchange.model.GetOrdersParams;
import org.altfund.xchangeinterface.xchange.model.Order;
import org.altfund.xchangeinterface.xchange.service.XChangeService;
import org.altfund.xchangeinterface.xchange.model.CurrenciesOnExchange;
import org.altfund.xchangeinterface.xchange.service.MessageEncryption;
import org.altfund.xchangeinterface.restApi.util.ResponseHandler;

/*
 * The above example does not specify GET vs. PUT, POST, and so forth, because
 * @RequestMapping maps all HTTP operations by default. Use
 * @RequestMapping(method=GET) to narrow this mapping.
 */
@Slf4j
@RestController
public class GetOrdersController {
    private final XChangeService xChangeService;
    private final JsonHelper jh;
    private final ResponseHandler rh;
    private final MessageEncryption messageEncryption;

    public GetOrdersController(XChangeService xChangeService, JsonHelper jh, ResponseHandler rh, MessageEncryption messageEncryption) {
        this.xChangeService = xChangeService;
        this.jh = jh;
        this.rh = rh;
        this.messageEncryption = messageEncryption;
    }

    @RequestMapping(value = "/getorders", produces = "application/json")
    public ResponseEntity<String> getOrders(@RequestParam Map<String, String> params) {
        GetOrdersParams ordersParams = null;
        String response = "";
        EncryptedOrder encryptedOrder = null;
        try {
            response = jh.getObjectMapper().writeValueAsString(params);
            log.debug("GET ORDERS CALLED {}", params);
            log.debug("GET ORDERS CALLED {}", response);
            encryptedOrder = jh.getObjectMapper().readValue(response, EncryptedOrder.class);

            //orderIds = jh.getObjectMapper().readValue(messageEncryption.decrypt(encryptedOrder), new TypeReference<List<String>>(){});
            //ordersParams = jh.getObjectMapper().readValue(encryptedOrder, GetOrdersParams.class);
            ordersParams = jh.getObjectMapper().readValue(messageEncryption.decrypt(encryptedOrder), GetOrdersParams.class);

            response = xChangeService.getOrders(ordersParams);
            log.debug("the response {}", response);

        }
        catch (JsonProcessingException ex) {
            log.debug("json ex {}", ex);
            return rh.send(ex, true);
        }
        catch (Exception ex) {
            log.debug("an ex {}", ex);
            //TODO error is logged here.
            return rh.send(ex, true);
        }
        //final HttpHeaders httpHeaders= new HttpHeaders();
        //httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        //return new ResponseEntity<String>(response, httpHeaders, HttpStatus.OK);
        return rh.send(response, true);
    }
}
