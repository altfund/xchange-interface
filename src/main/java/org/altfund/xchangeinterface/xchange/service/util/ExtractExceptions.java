package org.altfund.xchangeinterface.xchange.service.util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.altfund.xchangeinterface.util.JsonHelper;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import java.io.IOException;

public class ExtractExceptions {

    public static ObjectNode toJson(Exception e, JsonHelper jh) {
        ObjectNode errorMap = jh.getObjectNode();
        if (e instanceof IOException) {
            // Orders failed due to a network error can be retried.
            errorMap.put("ERROR", "Indication that a networking error occurred while fetching JSON data while fetching requested data on exchange " );
            return errorMap;
        } else if (e instanceof ExchangeException) {
            errorMap.put("ERROR", "Indication that the exchange reported some kind of error with the request or response while fetching requested data on exchange " );
            return errorMap;
        } else if (e instanceof IllegalArgumentException) {
            errorMap.put("ERROR", "Illegal argument exception while fetching requested data on exchange " );
            return errorMap;
        } else if (e instanceof NotAvailableFromExchangeException) {
            errorMap.put("ERROR", "Indication that the exchange does not support the requested function or data while fetching requested data on exchange " );
            return errorMap;
        } else if (e instanceof NotYetImplementedForExchangeException) {
            errorMap.put("ERROR", "Indication that the exchange supports the requested function or data, but it has not yet been implemented while fetching requested data on exchange " );
            return errorMap;
        } else {
            errorMap.put("ERROR", "Unknown error while fetching requested data on exchange " );
            return errorMap;
        }
    }
}
