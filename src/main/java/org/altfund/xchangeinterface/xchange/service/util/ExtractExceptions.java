package org.altfund.xchangeinterface.xchange.service.util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.altfund.xchangeinterface.xchange.model.OrderStatus;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.altfund.xchangeinterface.util.JsonHelper;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import java.io.IOException;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.PLACED;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.GENERAL_EXCHANGE_ERROR;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.NOT_AVAILABLE_FROM_EXCHANGE;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.NOT_YET_IMPLEMENTED_FOR_EXCHANGE;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.NETWORK_ERROR;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.UNKNOWN_ERROR;

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

    public static OrderStatus translate(Exception e) {
        if (e instanceof IOException) {
            e.printStackTrace();
            return new OrderStatus(NETWORK_ERROR, e);
            //return OrderStatus.NETWORK_ERROR;
        } else if (e instanceof ExchangeException) {
            //e.printStackTrace();
            //return OrderStatus.GENERAL_EXCHANGE_ERROR;
            return new OrderStatus(GENERAL_EXCHANGE_ERROR, e);
        } else if (e instanceof IllegalArgumentException) {
            //e.printStackTrace();
            //return OrderStatus.GENERAL_EXCHANGE_ERROR;
            return new OrderStatus(GENERAL_EXCHANGE_ERROR, e);
        } else if (e instanceof NotAvailableFromExchangeException) {
            e.printStackTrace();
            //return OrderStatus.NOT_AVAILABLE_FROM_EXCHANGE;
            return new OrderStatus(NOT_AVAILABLE_FROM_EXCHANGE, e);
        } else if (e instanceof NotYetImplementedForExchangeException) {
            e.printStackTrace();
            //return OrderStatus.NOT_YET_IMPLEMENTED_FOR_EXCHANGE;
            return new OrderStatus(NOT_YET_IMPLEMENTED_FOR_EXCHANGE, e);
        } else {
            return new OrderStatus(UNKNOWN_ERROR, e);
            //e.printStackTrace();
            //return OrderStatus.UNKNOWN_ERROR;
            //return new OrderStatus(UNKNOWN_ERROR, e);
        }
    }
}
