package org.altfund.xchangeinterface.restApi.util;

import java.util.Map;
import java.util.regex.Pattern;

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
import org.altfund.xchangeinterface.xchange.service.MessageEncryption;
import org.altfund.xchangeinterface.xchange.service.XChangeService;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import com.fasterxml.jackson.core.JsonProcessingException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;

import java.io.UnsupportedEncodingException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;


//TODO use jsonify exceptions?
//import org.altfund.xchangeinterface.xchange.service.util.JsonifyExceptions;

/*
 * The above example does not specify GET vs. PUT, POST, and so forth, because
 * @RequestMapping maps all HTTP operations by default. Use
 * @RequestMapping(method=GET) to narrow this mapping.
 */
@Slf4j
public class ResponseHandler {

    private MessageEncryption messageEncryption;

    public ResponseHandler(MessageEncryption messageEncryption) {
        this.messageEncryption = messageEncryption;
    }

    public String getErrorString(Exception ex) {
        String response = "";
        if (ex instanceof IOException) {
            response = "{ERROR: IOException "+ ex.getMessage() + "}";
        }
        else if (ex instanceof ExchangeException) {
            response = "{ERROR: ExchangeException "+ ex.getMessage() + "}";
        }
        else if (ex instanceof IllegalArgumentException) {
            response = "{ERROR: IllegalArgumentException  "+ ex.getMessage() + "}";
        }
        else if (ex instanceof NotAvailableFromExchangeException) {
            response = "{ERROR: NotAvailableFromExchangeException"+ ex.getMessage() + "}";
        }
        else if (ex instanceof NotYetImplementedForExchangeException) {
            response = "{ERROR: NotYetImplementedForExchangeException "+ ex.getMessage() + "}";
        }
        else if (ex instanceof NoSuchAlgorithmException) {
            response = "{ERROR: NoSuchAlgorithmException (error with encryption) "+ ex.getMessage() + "}";
        }
        else if (ex instanceof NoSuchPaddingException) {
            response = "{ERROR: NoSuchPaddingException (error with encryption) "+ ex.getMessage() + "}";
        }
        else if (ex instanceof InvalidKeyException) {
            response = "{ERROR: Invalid Key Exception (error with encryption) "+ ex.getMessage() + "}";
        }
        else if (ex instanceof IllegalBlockSizeException) {
            response = "{ERROR: Illegal Block Size Exception (error with encryption) "+ ex.getMessage() + "}";
        }
        else if (ex instanceof BadPaddingException) {
            response = "{ERROR: BadPaddingException (error with encryption) "+ ex.getMessage() + "}";
        }
        else if (ex instanceof UnsupportedEncodingException) {
            response = "{ERROR: UnsupportedEncodingException (error with encryption) "+ ex.getMessage() + "}";
        }
        return response;
    }

    public String send(Exception ex) {
        return getErrorString(ex);
    }

    public ResponseEntity<String> send(Exception ex, boolean doEncrypt) {
        return send(getErrorString(ex), doEncrypt);
    }

    public ResponseEntity<String> send(String res, boolean doEncrypt) {

        if (res == null || res == "" || res == "null")
            res = "{ERROR: No response, this error indicates an earlier error or issue was not handled properly.}";
        final HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (doEncrypt) {
            try {
                return new ResponseEntity<String>(messageEncryption.encrypt(res), httpHeaders, HttpStatus.OK);
            }
            catch (Exception ex) {
                log.error("ERROR while encrypting response {}\nstacktrade\n{}", ex.getMessage(), ex.getStackTrace());
                send(ex, false);
            }
        }
        return new ResponseEntity<String>(res, httpHeaders, HttpStatus.OK);
        /*
        if(Pattern.compile(".*exchange_credentials.*").matcher(res.replace("\n", "").replace("\r", "")).matches()) {
            log.debug("not transmitting credentials: {}", res);
            return new ResponseEntity<String>( res.substring(0, res.indexOf("exchange_credentials")) + " REDACTED", httpHeaders, HttpStatus.OK);
        }
        else  {
            log.debug("transmitting {}", res);
            return new ResponseEntity<String>(res, httpHeaders, HttpStatus.OK);
        }
        */
    }
}
