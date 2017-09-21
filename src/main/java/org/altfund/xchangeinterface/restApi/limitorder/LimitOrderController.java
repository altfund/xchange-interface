package org.altfund.xchangeinterface.restApi.limitorder;

import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.altfund.xchangeinterface.util.JsonHelper;
import org.altfund.xchangeinterface.xchange.model.EncryptedOrder;
import org.altfund.xchangeinterface.xchange.model.Order;
import org.altfund.xchangeinterface.xchange.model.OrderResponse;
import org.altfund.xchangeinterface.xchange.service.MessageEncryption;
import org.altfund.xchangeinterface.xchange.service.XChangeService;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;

import org.altfund.xchangeinterface.restApi.util.ResponseHandler;

/*
 * The above example does not specify GET vs. PUT, POST, and so forth, because
 * @RequestMapping maps all HTTP operations by default. Use
 * @RequestMapping(method=GET) to narrow this mapping.
 */
@RestController
@Slf4j
public class LimitOrderController {
    private final XChangeService xChangeService;
    private final JsonHelper jh;
    private final ResponseHandler rh;
    private EncryptedOrder encryptedOrder;
    private MessageEncryption messageEncryption;
    private Order order;

    public LimitOrderController(XChangeService xChangeService, JsonHelper jh, MessageEncryption messageEncryption, ResponseHandler rh) {
        this.xChangeService = xChangeService;
        this.jh = jh;
        this.rh = rh;
        this.messageEncryption = messageEncryption;
    }

    @RequestMapping(value = "/limitorder", produces = "application/json")
    public ResponseEntity<String> limitorder(@RequestParam Map<String, String> params) {
        String response = "";
        try {
            response = jh.getObjectMapper().writeValueAsString(params);
            log.debug("rec str {}.", response);
            encryptedOrder = jh.getObjectMapper().readValue(response, EncryptedOrder.class);
            log.debug("rec iv {}.", encryptedOrder.getIv());
            log.debug("rec data {}.", encryptedOrder.getEncryptedData());
            order = jh.getObjectMapper().readValue( messageEncryption.decrypt(encryptedOrder),
                                                                  Order.class);
            OrderResponse orderResponse = xChangeService.placeLimitOrder(order);
            response = jh.getObjectMapper().writeValueAsString(orderResponse);
            log.debug("The order response\n{}", response);
        }
        catch (Exception ex) {
            return rh.send(ex, true);
        }
        return rh.send(response, true);
    }
}
