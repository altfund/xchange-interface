package org.altfund.xchangeinterface.restApi.cancelorder;

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
import org.altfund.xchangeinterface.xchange.service.OrderDecryptor;
import org.altfund.xchangeinterface.xchange.service.XChangeService;
import org.altfund.xchangeinterface.restApi.util.ResponseHandler;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import com.fasterxml.jackson.core.JsonProcessingException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;


/*
 * The above example does not specify GET vs. PUT, POST, and so forth, because
 * @RequestMapping maps all HTTP operations by default. Use
 * @RequestMapping(method=GET) to narrow this mapping.
 */
@RestController
@Slf4j
public class CancelOrderController {
    private final XChangeService xChangeService;
    private final JsonHelper jh;
    private EncryptedOrder encryptedOrder;
    private OrderDecryptor orderDecryptor;
    private Order order;

    public CancelOrderController(XChangeService xChangeService, JsonHelper jh, OrderDecryptor orderDecryptor) {
        this.xChangeService = xChangeService;
        this.jh = jh;
        this.orderDecryptor = orderDecryptor;
    }

    @RequestMapping(value = "/cancelorder", produces = "application/json")
    public ResponseEntity<String> limitorder(@RequestParam Map<String, String> params) {
        String response = "";
        try {
            response = jh.getObjectMapper().writeValueAsString(params);
            log.debug("rec str {}.", response);
            encryptedOrder = jh.getObjectMapper().readValue(response, EncryptedOrder.class);
            log.debug("rec iv {}.", encryptedOrder.getIv());
            log.debug("rec data {}.", encryptedOrder.getEncryptedData());
            order = jh.getObjectMapper().readValue( orderDecryptor.decrypt(encryptedOrder),
                                                                  Order.class);
            //encrypted order needs to decrypt to a Map<String, String> :(
            //probs change that to a pojo like in ee.
            boolean orderResponse = xChangeService.cancelLimitOrder(order);
            response = jh.getObjectMapper().writeValueAsString(orderResponse);
        }
        catch (Exception ex) {
            return ResponseHandler.send(ex);
        }
        return ResponseHandler.send(response);
    }
}
