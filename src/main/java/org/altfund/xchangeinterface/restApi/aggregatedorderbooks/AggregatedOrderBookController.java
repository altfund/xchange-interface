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
import org.altfund.xchangeinterface.restApi.util.RequestHandler;
import org.altfund.xchangeinterface.util.JsonHelper;
import org.altfund.xchangeinterface.xchange.model.EncryptedOrder;
import org.altfund.xchangeinterface.xchange.model.MarketByExchanges;
import org.altfund.xchangeinterface.xchange.service.XChangeService;

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
    private final RequestHandler rq;

    public AggregatedOrderBookController(XChangeService xChangeService, JsonHelper jh, ResponseHandler rh, RequestHandler rq) {
        this.xChangeService = xChangeService;
        this.jh = jh;
        this.rh = rh;
        this.rq = rq;
    }

    @RequestMapping(value = "/aggregateorderbooks", produces = "application/json")
    public ResponseEntity<String> aggregateOrderBooks(@RequestBody Map<String, String> params) {
        MarketByExchanges marketByExchanges = null;
        String response = "";
        EncryptedOrder encryptedOrder;
        try {
            marketByExchanges = rq.decrypt(params, MarketByExchanges.class);
            //ObjectNode json = xChangeService.getAggregateOrderBooks(marketByExchanges);
            response = xChangeService.getAggregateOrderBooks(marketByExchanges);
            log.debug("response data {}.", response);
        }
        catch (JsonProcessingException ex) {
            return rh.send(ex, true);
        }
        catch (Exception ex) {
            return rh.send(ex, true);
        }
        return rh.send(response, true);
    }
}
